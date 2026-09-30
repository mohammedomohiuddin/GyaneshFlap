@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, org.apache.org/licenses/LICENSE-2.0
@rem

@if "%DEBUG%"=="" @echo off
@if "%JAVA_HOME%"=="" goto noJavaHome

set JAVA_EXE=%JAVA_HOME%\bin\java.exe

if not exist "%JAVA_EXE%" (
    echo JAVA_HOME is set to an invalid directory: %JAVA_HOME%
    exit /b 1
)

set GRADLE_WRAPPER_JAR=%~dp0gradle\wrapper\gradle-wrapper.jar

if not exist "%GRADLE_WRAPPER_JAR%" (
    echo Downloading Gradle Wrapper Jar...
    powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar' -OutFile '%GRADLE_WRAPPER_JAR%'"
)

"%JAVA_EXE%" -classpath "%GRADLE_WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
exit /b %ERRORLEVEL%

:noJavaHome
echo JAVA_HOME is not set!
exit /b 1
