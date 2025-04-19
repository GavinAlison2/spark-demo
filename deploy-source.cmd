
git config core.autocrlf false
git rm -r --cached .idea
git add .
git commit -m "deploy"
::  git remote add origin git@github.com:GavinAlison2/spark-demo.git
:: git branch -M master
:: git push -f -u origin master
git push -f origin master

