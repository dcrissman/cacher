[![Build Status](https://travis-ci.org/dcrissman/cacher.svg?branch=master)](https://travis-ci.org/dcrissman/cacher) [![Coverage Status](https://coveralls.io/repos/dcrissman/cacher/badge.png)](https://coveralls.io/r/dcrissman/cacher)

Cacher
------
https://github.com/dcrissman/cacher

Project Goal
-------------
Standardize and abstract the way that caching is accessed and create hooks to ease implementation without coupling core logic to caching mechanism.

Description
------------
Cacher provides a simple interface (Cache) that is designed to wrap a 3rd party caching client/library (eg. spymemcached). This interface is then consumed by the FetchManager which houses logic to standarize how cached values are stored and retrieved. If a value is already cached then it is simply returned, otherwise a Fetcher (either FetchSingle or FetchMultiple) is asked how to produce the value, which is then cached for subsequent requests.

NOTE: Currently only Memcached is supported, but the hope is to add more as it makes sense.


Special Thanks to Andrew Edwards as much of this library is based on his original work.

AOP
---
CacheInterceptor and @FetcherMethod are also provided to add AOP support.


Copyright and License
---------------------

  Copyright 2013 Red Hat, Inc.
  Author: Dennis Crissman

  Licensed under the GNU Lesser General Public License, version 3 or
  any later version.

  In addition to the conditions of LGPLv3, you must preserve author
  attributions in source code distributions.
