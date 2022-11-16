/********************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the Eclipse Distribution License
 * v1.0 which is available at
 * https://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 ********************************************************************************/
package org.eclipse.starter.cli;

import org.apache.maven.shared.invoker.*;
import picocli.CommandLine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "jakartaee", mixinStandardHelpOptions = true, version = "jakartaee 1.0.0",
        description = "Get started with Jakarta EE")
class JakartaEE implements Callable<Integer> {

    private static final Map<String, String> ARCHETYPE_VERSIONS = new HashMap<>();
    private static final Map<String, String> PROFILES = new HashMap<>();

    static {
        ARCHETYPE_VERSIONS.put("8", "1.0.0");
        ARCHETYPE_VERSIONS.put("9.1", "1.0.0");
        ARCHETYPE_VERSIONS.put("10", "1.1.0");

        PROFILES.put("core", "core-api");
        PROFILES.put("web", "web-api");
        PROFILES.put("platform", "api");
    }

    @Parameters(index = "0", description = "init, dev, run")
    private String command;

    @Option(names = {"-v", "--version"}, description = "8, 9.1, 10")
    private String version = "10";

    @Option(names = {"-p", "--profile"}, description = "core, web, platform")
    private String profile = "platform";

    @Override
    public Integer call() throws Exception { // your business logic goes here...

        System.out.printf("Initializing a Jakarta EE %s %s Profile project", version, profile.substring(0,1).toUpperCase() + profile.substring(1).toLowerCase());
        System.out.println();

        return 0;
    }

    public static void main(String... args) {

        JakartaEE jakartaEE = new JakartaEE();

        int exitCode = new CommandLine(jakartaEE).execute(args);

//        String mvnPath = findExecutable("mvn");
        System.setProperty("maven.home", "/opt/homebrew/bin/mvn");
        InvocationRequest request = new DefaultInvocationRequest();
        request.setGoals(Collections.singletonList("archetype:generate"));
        request.setBatchMode(true);
        Properties properties = new Properties();
        properties.setProperty("groupId", "com.example");
        properties.setProperty("artifactId", "demo");
        properties.setProperty("archetypeVersion", "1.0.0-SNAPSHOT");
        properties.setProperty("profile", PROFILES.get(jakartaEE.profile));
        properties.setProperty("archetypeGroupId", "org.eclipse.starter");
        properties.setProperty("archetypeArtifactId", "jakartaee" + jakartaEE.version + "-minimal");
        properties.setProperty("archetypeVersion", ARCHETYPE_VERSIONS.get(jakartaEE.version));
        request.setProperties(properties);

        Invoker invoker = new DefaultInvoker();
        try {
            InvocationResult result = invoker.execute(request);
        } catch (MavenInvocationException e) {
            throw new RuntimeException(e);
        }

        System.exit(exitCode);
    }
}
