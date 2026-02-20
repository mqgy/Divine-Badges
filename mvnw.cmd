@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM
@REM Required ENV vars:
@REM   JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars:
@REM   MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM       set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM   MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@REM Begin all REM:/sym::sym lines in this script as batch labels
@REM to guarantee batch file will not fail for any user configuration
@REM of line endings (LF vs CRLF) or other "batch" issues.

@IF "%~1" == "" @ECHO Usage: %~nx0 ^<Maven goals and options^> & @GOTO :MVNEnd

@REM set %HOME% to equivalent of $HOME
@IF NOT "%HOME%"=="" GOTO valMHome
@SET "HOME=%HOMEDRIVE%%HOMEPATH%"

:valMHome
@SET MAVEN_PROJECTBASEDIR=%~dp0
@SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

@SET WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPER_PROPERTIES="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"

@REM Extension to allow automatically downloading the maven-wrapper.jar from Maven Central
@REM This allows using the wrapper in projects that prohibit checking the jar into source control.

@IF EXIST %WRAPPER_JAR% (
    @IF "%MVNW_VERBOSE%" == "true" (
        ECHO Found %WRAPPER_JAR%
    )
) ELSE (
    @IF NOT "%MVNW_REPOURL%" == "" (
        SET WRAPPER_URL="%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
    )
    @IF "%WRAPPER_URL%" == "" (
        FOR /F "usebackq tokens=1,2 delims==" %%A IN (%WRAPPER_PROPERTIES%) DO (
            @IF "%%A"=="wrapperUrl" SET WRAPPER_URL=%%B
        )
    )

    @IF "%MVNW_VERBOSE%" == "true" (
        ECHO Downloading from: %WRAPPER_URL%
    )

    powershell -Command "&{"^
        "$webclient = new-object System.Net.WebClient;"^
        "if (-not ([string]::IsNullOrEmpty('%MVNW_USERNAME%') -and [string]::IsNullOrEmpty('%MVNW_PASSWORD%'))) {"^
        "$webclient.Credentials = new-object System.Net.NetworkCredential('%MVNW_USERNAME%', '%MVNW_PASSWORD%');"^
        "}"^
        "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $webclient.DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')"^
        "}"
    @IF "%MVNW_VERBOSE%" == "true" (
        ECHO Finished downloading %WRAPPER_JAR%
    )
)

@REM Provide a "standardized" way to retrieve the CLI args that will
@REM work with both Windows and non-Windows executions.
@SET MAVEN_CMD_LINE_ARGS=%*

@REM Find java.exe
@IF NOT "%JAVA_HOME%"=="" GOTO valJHome

@ECHO.
@ECHO Error: JAVA_HOME not found in your environment. >&2
@ECHO Please set the JAVA_HOME variable in your environment to match the >&2
@ECHO location of your Java installation. >&2
@ECHO.
@GOTO error

:valJHome
@SET "JAVA_HOME=%JAVA_HOME:"=%"
@SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"

@IF EXIST "%JAVA_EXE%" GOTO chkMHome

@ECHO.
@ECHO Error: JAVA_HOME is set to an invalid directory. >&2
@ECHO JAVA_HOME = "%JAVA_HOME%" >&2
@ECHO Please set the JAVA_HOME variable in your environment to match the >&2
@ECHO location of your Java installation. >&2
@ECHO.
@GOTO error

:chkMHome
@IF NOT "%MAVEN_HOME%"=="" GOTO valMvnHome
@GOTO runM

:valMvnHome
@IF EXIST "%MAVEN_HOME%\bin\mvn.cmd" GOTO runMvnHome
@ECHO.
@ECHO Warning: MAVEN_HOME is set but mvn.cmd was not found in "%MAVEN_HOME%\bin"
@ECHO.

:runMvnHome
@SET "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"
%MAVEN_CMD% %*
@GOTO MVNEnd

:runM
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain
"%JAVA_EXE%" ^
  %JVM_CONFIG_MAVEN_PROPS% ^
  %MAVEN_OPTS% ^
  %MAVEN_DEBUG_OPTS% ^
  -classpath %WRAPPER_JAR% ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  %WRAPPER_LAUNCHER% %MAVEN_CMD_LINE_ARGS%
@IF ERRORLEVEL 1 GOTO error
@GOTO MVNEnd

:error
@SET ERROR_CODE=1

:MVNEnd
@ENDLOCAL & @SET ERROR_CODE=%ERROR_CODE%

@IF NOT "%MAVEN_SKIP_RC%"=="" GOTO skipRcPost
@REM check for post script, once with legacy .bat ending and once with .cmd ending
@IF EXIST "%USERPROFILE%\mavenrc_post.bat" CALL "%USERPROFILE%\mavenrc_post.bat"
@IF EXIST "%USERPROFILE%\mavenrc_post.cmd" CALL "%USERPROFILE%\mavenrc_post.cmd"
:skipRcPost

@REM pause the script if MAVEN_BATCH_PAUSE is set to 'on'
@IF "%MAVEN_BATCH_PAUSE%"=="on" PAUSE

@IF "%MAVEN_TERMINATE_CMD%"=="on" EXIT %ERROR_CODE%

CMD /C EXIT /B %ERROR_CODE%
