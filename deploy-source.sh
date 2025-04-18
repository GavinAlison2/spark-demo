#!/usr/bin/env sh

set -e
git init .

git add -A

#git rm -r --cached .idea

git commit -m 'deploy spark demo to github'

git config --global user.name "GavinAlison2"
git config --global user.email "921757697@qq.com"

#git remote add origin git@github.com:GavinAlison2/spark-demo.git
git branch -M master
git push -f -u origin master

