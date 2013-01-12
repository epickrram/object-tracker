//////////////////////////////////////////////////////////////////////////////////
//   Copyright 2011   Mark Price     mark at epickrram.com                      //
//                                                                              //
//   Licensed under the Apache License, Version 2.0 (the "License");            //
//   you may not use this file except in compliance with the License.           //
//   You may obtain a copy of the License at                                    //
//                                                                              //
//       http://www.apache.org/licenses/LICENSE-2.0                             //
//                                                                              //
//   Unless required by applicable law or agreed to in writing, software        //
//   distributed under the License is distributed on an "AS IS" BASIS,          //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   //
//   See the License for the specific language governing permissions and        //
//   limitations under the License.                                             //
//////////////////////////////////////////////////////////////////////////////////

package com.epickrram.tool.tracker.agent;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class Agent implements ClassFileTransformer
{
    private static final Logger LOGGER = Logger.getLogger(Agent.class.getName());
    private final AgentConfig agentConfig;

    public static void premain(final String agentArgs, final Instrumentation instrumentation)
    {
        LOGGER.info("Adding Agent instance as ClassFileTransformer");
        instrumentation.addTransformer(new Agent());

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable()
        {
            public void run()
            {
                ObjectCreationListenerSingleton.INSTANCE.updateStats();
            }
        }, 10000L, 1000L, TimeUnit.MILLISECONDS);
    }

    public Agent()
    {
        agentConfig = new AgentConfig();
    }

    public byte[] transform(final ClassLoader loader, final String classPathName,
                            final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain,
                            final byte[] classfileBuffer) throws IllegalClassFormatException
    {
        final String className = convertToDottedPackage(classPathName);

        if(isExcluded(className))
        {
            return classfileBuffer;
        }

        LOGGER.info(String.format("Tracking instance creation for %s", className));

        try
        {
            final ClassPool pool = ClassPool.getDefault();
            final CtClass cls = pool.get(className);
            final CtConstructor[] constructors = cls.getDeclaredConstructors();
            for (CtConstructor constructor : constructors)
            {
                if (constructor.callsSuper())
                {
                    constructor.insertBeforeBody(generateCallToInstanceListener());
                }
            }
            return cls.toBytecode();
        }
        catch (NotFoundException e)
        {
            logWarning(String.format("Unable to transform class %s", classPathName), e);
        }
        catch (CannotCompileException e)
        {
            logWarning(String.format("Unable to transform class %s", classPathName), e);
        }
        catch (IOException e)
        {
            logWarning("Failed to convert class file", e);
        }
        catch(RuntimeException e)
        {
            logWarning("Failed to process", e);
        }

        return classfileBuffer;
    }

    private boolean isExcluded(final String className)
    {
        final boolean included = matchesFilter(className, agentConfig.getIncludeFilters());
        final boolean excluded = matchesFilter(className, agentConfig.getExcludeFilters());

        return excluded || !included;
    }

    private boolean matchesFilter(final String className, final Collection<Pattern> filters)
    {
        boolean included = false;
        for (Pattern filter : filters)
        {
            if(filter.matcher(className).find())
            {
                included = true;
                break;
            }
        }
        return included;
    }

    private String convertToDottedPackage(final String className)
    {
        return className.replace('/', '.');
    }

    private String generateCallToInstanceListener()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append(ObjectCreationListenerSingleton.class.getName()).append(".INSTANCE.onObjectCreation(getClass());");
        return builder.toString();
    }

    private static void logWarning(final String msg, final Throwable exception)
    {
        LOGGER.log(Level.WARNING, String.format("%s: %s", msg, exception.getMessage()));
    }
}