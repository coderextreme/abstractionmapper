#!/bin/bash
kill `ps | grep -E 'rmiregistry|java' |awk '{ print $1; }'`
