@echo Running python server...
@echo To end press CTRL + C and then N to restore dir if running from cmd line
:: this stores current dir
pushd %~dp0
cd %~dp0html\build\dist
python -m SimpleHTTPServer 8080
:: this restores last dir
popd
