# Introduction

**Notice:** **Use this repsitory at your own risk!** 

**Notice:** do not do `mvn install` from anything in this repository or its submodules until groupId is adjusted for each branch. Otherwise, you risk having a very confusing set of artifacts in your local repository especially since each branch will likely create somewhat different artifacts but, so far, still have the same Maven coordinates. I will change the groupId per branch as I have a better understanding of where this is really needed. You can also use dedicated local repositories to avoid issues with your `~\.m2` user-wide local repository. You can override the local repository on the command line with `mvn -Dmaven.repo.local=some/repo/locaion ...` or by configuring other tools to use a different repository than the `~\.m2` local repository. Of course, you can always delete your `~\.m2\repository` folder to start fresh when needed. I'd suggest you rename it first so something like `~\.m2\repository_2022.03.17` before deciding to delete that content.



This repository is created to facilitate working with [the hapi-fhir source code](https://github.com/hapifhir/hapi-fhir), which is a git submodule under /hapi-fhir, for my own purposes but it might be useful for others as well.

Please see the README_branch-name.md files as well to know what a branch in this repository is about. It is likely that each branch will represent a different arrangement of hapi-fhir source, "modules", and/or "projects", to prototype or demo something independent of what other branches might represent. The README_branch-name.md files are meant to have some minimal documentation/log of important information, mostly to help me remember things. I will expand on the documentation/log as I realize what might be useful for others.

Please open an issue as needed to ask for help . I'll do my best to keep up with help requests, time permitting.

This repository currently has two git submodules, one under `/hapi-fhir` and the other under `/projects/jpaserver-starter-build/jpaserver-starter`. See the `.gitmodules` file for more details, and make sure you use git submodule update as needed to make sure the submodule's state is up-to-date with your repository checkout.  The submodules are based on my forks or the corresponding upstream repositories.


