#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
#
# Required ENV vars:
#   JAVA_HOME - location of a JDK home dir
#
# Optional ENV vars:
#   MAVEN_OPTS - parameters passed to the Java VM when running Maven
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_SKIP_RC" ] ; then
  if [ -f /usr/local/etc/mavenrc ] ; then
    . /usr/local/etc/mavenrc
  fi
  if [ -f /etc/mavenrc ] ; then
    . /etc/mavenrc
  fi
  if [ -f "$HOME/.mavenrc" ] ; then
    . "$HOME/.mavenrc"
  fi
fi

# OS specific support
cygwin=false;
darwin=false;
mingw=false
case "$(uname)" in
  CYGWIN*) cygwin=true ;;
  MINGW*)  mingw=true ;;
  Darwin*) darwin=true ;;
esac

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
MAVEN_CMD_LINE_ARGS="$@"
export MAVEN_CMD_LINE_ARGS
export MAVEN_PROJECTBASEDIR

WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

# Determine Java command
if [ -n "$JAVA_HOME" ] ; then
  if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
    JAVACMD="$JAVA_HOME/jre/sh/java"
  else
    JAVACMD="$JAVA_HOME/bin/java"
  fi
  if [ ! -x "$JAVACMD" ] ; then
    echo "Error: JAVA_HOME is set to an invalid directory: $JAVA_HOME" >&2
    exit 1
  fi
else
  JAVACMD="$(which java)"
  if [ ! -x "$JAVACMD" ] ; then
    echo "Error: JAVA_HOME is not set and java is not in your PATH." >&2
    exit 1
  fi
fi

if $cygwin ; then
  JAVACMD="$(cygpath --unix "$JAVACMD")"
  [ -n "$JAVA_HOME" ] && JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
fi

# Download maven-wrapper.jar if it doesn't exist
if [ ! -e "$WRAPPER_JAR" ] ; then
  if [ -n "$MVNW_REPOURL" ] ; then
    wrapperUrl="$MVNW_REPOURL/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
  else
    wrapperUrl=$(grep 'wrapperUrl' "$WRAPPER_PROPERTIES" | cut -d'=' -f2-)
  fi

  if [ -z "$wrapperUrl" ] ; then
    echo "Error: Could not determine wrapper URL from $WRAPPER_PROPERTIES" >&2
    exit 1
  fi

  if [ "$MVNW_VERBOSE" = true ] ; then
    echo "Downloading from: $wrapperUrl"
  fi

  if command -v wget > /dev/null; then
    if [ "$MVNW_VERBOSE" = true ] ; then
      echo "Found wget... using wget"
    fi
    if [ -z "$MVNW_USERNAME" ] || [ -z "$MVNW_PASSWORD" ] ; then
      wget -q "$wrapperUrl" -O "$WRAPPER_JAR" || rm -f "$WRAPPER_JAR"
    else
      wget -q --http-user="$MVNW_USERNAME" --http-password="$MVNW_PASSWORD" "$wrapperUrl" -O "$WRAPPER_JAR" || rm -f "$WRAPPER_JAR"
    fi
  elif command -v curl > /dev/null; then
    if [ "$MVNW_VERBOSE" = true ] ; then
      echo "Found curl... using curl"
    fi
    if [ -z "$MVNW_USERNAME" ] || [ -z "$MVNW_PASSWORD" ] ; then
      curl -fsSL -o "$WRAPPER_JAR" "$wrapperUrl"
    else
      curl -fsSL --user "$MVNW_USERNAME:$MVNW_PASSWORD" -o "$WRAPPER_JAR" "$wrapperUrl"
    fi
  else
    echo "Error: Could not find curl or wget to download maven-wrapper.jar" >&2
    exit 1
  fi
fi

# Cygwin paths
if $cygwin ; then
  WRAPPER_JAR="$(cygpath --path --windows "$WRAPPER_JAR")"
  MAVEN_PROJECTBASEDIR="$(cygpath --path --windows "$MAVEN_PROJECTBASEDIR")"
fi

exec "$JAVACMD" \
  $JVM_CONFIG_MAVEN_PROPS \
  $MAVEN_OPTS \
  $MAVEN_DEBUG_OPTS \
  -classpath "$WRAPPER_JAR" \
  "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
