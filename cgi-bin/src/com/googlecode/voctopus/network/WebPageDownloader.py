#!/usr/bin/env python

import urllib2
import sys

def getUrlLines(url):
    for line in urllib2.urlopen(url):
        print line

if __name__ == '__main__':
    getUrlLines(sys.argv[1])
