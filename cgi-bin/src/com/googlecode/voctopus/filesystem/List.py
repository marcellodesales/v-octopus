#!/usr/bin/env python
import os
import sys
import string

def main(args):
    if (args < 2):
        sys.exit("The directory argument 'dir' is necessary")
    dirPath = ""
    try:
        dirPath = args[1][string.index(args[1], '=') + 1:] 
        #string.index throws exception ValueError
        #string.find returns -1 and doesn't throw any exception
    except:
        sys.exit("The directory argument format is dir=PATH'")

    try: 
        for name in os.listdir(dirPath):
            if (os.path.isdir(dirPath + "/" + name)):
                print "d]" + name
            else:
                print name
    except OSError, error:
        sys.exit(error);

if __name__ == '__main__':
    main(sys.argv)