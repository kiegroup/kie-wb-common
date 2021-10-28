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

We do "simple" maven builds, they are just basically maven commands, but just because we have multiple repositories related between them and one change could affect several of those projects by multiple pull requests, we use [build-chain tool](https://github.com/kiegroup/github-action-build-chain) to handle cross repository builds and be sure that we always use latest version of the code for each repository.

[build-chain tool](https://github.com/kiegroup/github-action-build-chain) is not only a github-action tool but a CLI one, so in case you posted multiple pull requests related with this change you can easily reproduce the same build by executing it locally. See [local execution](https://github.com/kiegroup/github-action-build-chain#local-execution) details to get more information about it.
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
