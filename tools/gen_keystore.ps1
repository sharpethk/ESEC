$ErrorActionPreference = 'Stop'
$kt = 'C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe'
$ks = 'D:\Android Apps\ESEC\app\keystores\esec-release.jks'
if (Test-Path $ks) { Remove-Item $ks -Force }
& $kt -genkeypair -v `
  -keystore $ks `
  -alias esec `
  -keyalg RSA -keysize 2048 -validity 9125 `
  -storepass 'esec_release_storepass' `
  -keypass 'esec_release_keypass' `
  -dname 'CN=ESEC ExamPrep, OU=Apps, O=ESEC, L=Asmara, C=ER'
Write-Host "--- LIST ---"
& $kt -list -v -keystore $ks -storepass 'esec_release_storepass' -alias esec | Select-Object -First 25
