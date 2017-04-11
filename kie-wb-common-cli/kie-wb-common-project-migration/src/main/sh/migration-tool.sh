#!/bin/sh

export CLASSPATH="`find lib -name '*.jar' | tr '\n' ':' | sed -E 's/:$//'`"

java ${mainClass} $@

