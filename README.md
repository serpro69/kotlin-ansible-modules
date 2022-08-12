# kotlin-ansible-modules

Examples of writing ansible modules using kotlin.

I've never been a fan of dynamic languages in general and Python in particular. But working a lot with Ansible, I do, however, feel that sometimes an *ansible role* doesn't cut it, and it would be better to write a *module*. I've experimented a bit with [using golang for writing ansible modules](https://github.com/serpro69/golang-ansible-modules), and while it works great for the most part, the one problem I have with using golang for developing ansible modules is it that the module needs to be built and used as a binary (I wish one day ansible will allow to run `.go` files directly as modules.) Therefore, using a standalone script is preferable because you don't need to do any extra movements to be able to use it as a module (apart from having the dependency for the script installed, of course.) Ansible has some very basic examples on writing non-native modules in [ansible-for-rubyists](https://github.com/ansible/ansible-for-rubyists) repo.

But why kotlin? It's simple really. Although not without its own faults, I think kotlin is generally great and I love using kotlin for all sorts of things, from test automation to backend. In addition to that, I was one of the two people who introduced kotlin and heavily pushed for it to be used alongside java at my current work, and now some years later we're a "kotlin-first shop". So yeah, if I have a choice, I'll likely pick kotlin over python and most other languages because I like it.

Thankfully, in recent years, kotlin has matured its scripting capabilities also, and while doing a binary-type module in kotlin is a definite red light for obvious reasons (talk about compiling a native binary and distributing it within ansible's ecosystem...), it occurred to me that kotlin scripting could in fact be used for this purpose quite nicely, especially since it now (since 1.4 actually, if I recall correctly) supports shebang and can be executed as a regular script without needing to pass the script file to kotlin compiler.

*It is worth noting that in many cases python is still much more preferable to use when developing ansible modules.*

## Code Structure

This repo consists of the following:

- [ansible-collection-helloworld](ansible-collection-helloworld) - source code for a basic "hello-world" ansible collection that contains two modules:
  - [includejson](ansible-collection-helloworld/io_github_serpro69/helloworld_kotlin/plugins/modules/includejson.main.kts) - an implementation of a [JSONARGS module](https://docs.ansible.com/ansible/latest/dev_guide/developing_program_flow_modules.html#jsonargs-modules)
  - [wantjson](ansible-collection-helloworld/io_github_serpro69/helloworld_kotlin/plugins/modules/wantjson.main.kts) - an implementation of a [Non-native want JSON module](https://docs.ansible.com/ansible/latest/dev_guide/developing_program_flow_modules.html#non-native-want-json-modules)
  - the only difference between the two is how the arguments are parsed from ansible to the module
- [test-playbook](test-playbook) - a test playbook that runs both modules from the collection

## Usage

Run `make play` to execute a sample [test playbook](test-playbook/test.yml) which will run various tasks using both modules.

## Testing / Debugging

To test and debug a module with a local json arguments file (for the "Non-native want JSON" type modules):

```
cd ansible-collection-helloworld/io_github_serpro69/helloworld_kotlin/plugins
./modules/wantjson.main.kts ./test/wantjson-test.json
```

It is not possible to test the "JSONARGS module" in the same way (via local json argument file) because ansible replaces the `<<INCLUDE_ANSIBLE_MODULE_JSON_ARGS>>` placeholder string with the json-formatted argument string at runtime. For JSONARGS-type module, the `test-module` script might be useful for testing to avoid going through the entire ansible workflow (the script is available in the [`hacking`](https://github.com/ansible/ansible/tree/devel/hacking) dir of the Ansible git repo):

```
cd ansible-collection-helloworld/io_github_serpro69/helloworld_kotlin/plugins
wget -O test-module https://raw.githubusercontent.com/ansible/ansible/devel/hacking/test-module.py
chmod +x test-module
./test-module -m ./modules/includejson.main.kts -o /tmp/includejson.main.kts -a "name='include jsonargs'"
```

Notice the `-o /tmp/includejson.main.kts` option passed to the `test-module` script. This is needed because the default output is `~/.ansible_module_generated`, but this will result in an error coming from kotlin :

```
error: unrecognized script type: .ansible_module_generated; Specify path to the script file as the first argument
```

This happens because we are using [kotlin-main-kts](https://github.com/Kotlin/kotlin-script-examples/blob/master/jvm/main-kts/MainKts.md), which requires the `.main.kts` extension for the script file.

## Limitations

Writing ansible modules in kotlin script does not suffer from same distribution problems as binary-based modules. (See [golang-ansible-modules#limitations](https://github.com/serpro69/golang-ansible-modules#limitations) for more details.) The only downside of this approach is of course that kotlin needs to be installed. Since we use kotlin and java predominantly in our application code, this isn't really a problem. But this needs to be considered nevertheless when deciding to write ansible modules in a different language than python.

Kotlin script also needs to be compiled, which delays the module startup. Might not be that important since it only takes a second or two at most, but it's definitely slower than running a binary-based module or a python script, for example.
