**Thank you for submitting this pull request**

**JIRA**: [JBPM-0000](https://issues.redhat.com/browse/JBPM-0000)

**Referenced Pull Requests**:
* paste the link(s) from GitHub here
* link 2
* link 3 etc.

**Business Central**: [WAR file](https://donwnload.here/something)

<details>
<summary>
How to replicate CI configuration locally?
</summary>

Build Chain tool does "simple" maven build(s), the builds are just Maven commands, but because the repositories relates and depends on each other and any change in API or class method could affect several of those repositories there is a need to use [build-chain tool](https://github.com/kiegroup/github-action-build-chain) to handle cross repository builds and be sure that we always use latest version of the code for each repository.
 
[build-chain tool](https://github.com/kiegroup/github-action-build-chain) is a build tool which can be used on command line locally or in Github Actions workflow(s), in case you need to change multiple repositories and send multiple dependent pull requests related with a change you can easily reproduce the same build by executing it on Github hosted environment or locally in your development environment. See [local execution](https://github.com/kiegroup/github-action-build-chain#local-execution) details to get more information about it.
</details>

<details>
<summary>
How to retest this PR or trigger a specific build:
</summary>

* Retest PR: <b>jenkins retest this</b>
* A full downstream build: <b>jenkins do fdb</b>
* A compile downstream build: <b>jenkins do cdb</b>
* A full production downstream build: <b>jenkins do product fdb</b>
* An upstream build: <b>jenkins do upstream</b>
</details>

<details>
<summary>
How to backport a pull request to a different branch?
</summary>

In order to automatically create a **backporting pull request** please add one or more labels having the following format `backport-<branch-name>`, where `<branch-name>` is the name of the branch where the pull request must be backported to (e.g., `backport-7.67.x` to backport the original PR to the `7.67.x` branch).

> **NOTE**: **backporting** is an action aiming to move a change (usually a commit) from a branch (usually the main one) to another one, which is generally referring to a still maintained release branch. Keeping it simple: it is about to move a specific change or a set of them from one branch to another.

Once the original pull request is successfully merged, the automated action will create one backporting pull request per each label (with the previous format) that has been added.

If something goes wrong, the author will be notified and at this point a manual backporting is needed.

> **NOTE**: this automated backporting is triggered whenever a pull request on `main` branch is labeled or closed, but both conditions must be satisfied to get the new PR created.
</details>
