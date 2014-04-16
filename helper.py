#!/usr/bin/python
f = open('data.txt')
content = f.read().splitlines()
content = list(set(content))
f.close()

f = open('data-no-duplicates.txt', 'w')
for line in content:
    print >> f, line
f.close()
