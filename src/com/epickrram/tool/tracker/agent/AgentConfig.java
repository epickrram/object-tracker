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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.regex.Pattern;

final class AgentConfig
{
    private static final Logger LOGGER = Logger.getLogger(AgentConfig.class.getName());

    private static final String INCLUDE_FILTER_KEY = "com.epickrram.tool.object-tracker.config.include";
    private static final String EXCLUDE_FILTER_KEY = "com.epickrram.tool.object-tracker.config.exclude";
    private static final String FILTER_FILE_KEY = "com.epickrram.tool.object-tracker.config.file";

    private final Collection<Pattern> includeFilters = new ArrayList<Pattern>();
    private final Collection<Pattern> excludeFilters = new ArrayList<Pattern>();

    AgentConfig()
    {
        if(System.getProperty(FILTER_FILE_KEY) != null)
        {
            parseFilterFile(System.getProperty(FILTER_FILE_KEY));
        }
        else if(System.getProperty(INCLUDE_FILTER_KEY) != null)
        {
            includeFilters.addAll(parseLine(System.getProperty(INCLUDE_FILTER_KEY)));
        }
        else if(System.getProperty(EXCLUDE_FILTER_KEY) != null)
        {
            excludeFilters.addAll(parseLine(System.getProperty(EXCLUDE_FILTER_KEY)));
        }

        LOGGER.info(String.format("Include: %s", includeFilters));
        LOGGER.info(String.format("Exclude: %s", excludeFilters));
    }

    final Collection<Pattern> getIncludeFilters()
    {
        return includeFilters;
    }

    final Collection<Pattern> getExcludeFilters()
    {
        return excludeFilters;
    }

    private void parseFilterFile(final String filename)
    {
        LOGGER.info(String.format("Parsing filters from file %s", filename));
        final File configFile = new File(filename);
        if(configFile.exists())
        {
            try
            {
                final BufferedReader reader = new BufferedReader(new FileReader(configFile));
                String line;
                while((line = reader.readLine()) != null)
                {
                    handleConfigFileLine(line);
                }
                reader.close();
            }
            catch (IOException e)
            {
                LOGGER.warning(String.format("Unable to parse config file: %s, due to %s", filename, e.getMessage()));
            }
        }
        else
        {
            LOGGER.warning(String.format("Cannot file specified config file: %s", filename));
        }
    }

    private void handleConfigFileLine(final String line)
    {
        if(line.startsWith(INCLUDE_FILTER_KEY))
        {
            includeFilters.addAll(parseLine(line.substring(line.indexOf('=') + 1)));
        }
        else if(line.startsWith(EXCLUDE_FILTER_KEY))
        {
            excludeFilters.addAll(parseLine(line.substring(line.indexOf('=') + 1)));
        }
    }

    private static Collection<Pattern> parseLine(final String line)
    {
        final String[] elements = line.split(";");
        final Collection<Pattern> patterns = new ArrayList<Pattern>(elements.length);
        for (String element : elements)
        {
            compileToPattern(element, patterns);
        }

        return patterns;
    }

    private static void compileToPattern(final String element, final Collection<Pattern> patterns)
    {
        try
        {
            patterns.add(Pattern.compile(element));
        }
        catch (Exception e)
        {
            LOGGER.warning(String.format("Failed to compile regex pattern: %s", element));
        }
    }
}