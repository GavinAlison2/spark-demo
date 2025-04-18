#!/usr/bin/env sh

# set -e
git init .

git add -A

git rm -r --cached .idea

git commit -m 'doc 整理'

git remote add origin git@github.com:GavinAlison2/spark-demo.git

git pull origin master --allow-unrelated-histories
# git remote add origin git@github.com:GavinAlison2/simple-doc.git
git branch -M master
# git push  -f origin master:gh-pages
git push -f origin master

