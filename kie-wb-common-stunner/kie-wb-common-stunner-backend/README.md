Stunner - Default backend services implementations
===================================================

This module provides some backend implementations on top of the Stunner backend-api and backend-common modules.

It defaults by creating a defauilt file system into the VFS and the services refer to it.

**IMPORTANT**
DO NOT use this module if you rely on the _project_ integration modules. Otherwise another repository and additional stuff will be generated as well as with the (guvnor/KIE) project stuff.
