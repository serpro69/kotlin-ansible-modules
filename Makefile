.ONESHELL:

all: play

play:
	cd test-playbook
	ansible-galaxy install -r requirements.yml --force && \
	ansible-playbook test.yml -c local -i hosts -v
	cd ..
