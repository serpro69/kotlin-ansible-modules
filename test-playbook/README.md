# test-playbook-git-source

Runs the `includejson` and `wantjson` modules from the collection which is installed from the local directory (this same repo).

## Requirements

* kotlin

## Usage

```
ansible-galaxy install -r requirements.yml --force
ansible-playbook test.yml -v
```
