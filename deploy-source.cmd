#echo off

git config core.autocrlf false
git add .
git rm -r --cached .idea
git commit -m 'deploy spark-sql demo to github'
:: @REM  git remote add origin git@github.com:GavinAlison2/spark-demo.git
:: git branch -M master
:: git push -f -u origin master
git push -f origin master

:: 检查上一条命令的执行结果
if %errorlevel% neq 0 (
    echo 推送代码到远程仓库失败，请检查错误信息。
    exit /b %errorlevel%
)

:: 如果需要，可以在这里添加更多的错误处理或成功信息
echo 代码已成功推送到远程仓库。