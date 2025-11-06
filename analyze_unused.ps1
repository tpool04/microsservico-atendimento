# PowerShell script to find Java classes in src/main/java referenced only once (likely unused)
$root = 'c:\workspace\microsservico-atendimento'
$files = Get-ChildItem -Path $root -Recurse -Filter *.java | Where-Object { $_.FullName -match '\\src\\main\\java\\' }
$allPaths = $files | Select-Object -ExpandProperty FullName
$results = @()
foreach ($f in $files) {
    $name = [IO.Path]::GetFileNameWithoutExtension($f.Name)
    $pattern = '\b' + [regex]::Escape($name) + '\b'
    $count = (Select-String -Path $allPaths -Pattern $pattern -AllMatches -ErrorAction SilentlyContinue | Measure-Object).Count
    if ($count -le 1) {
        $results += [PSCustomObject]@{Name=$name; Count=$count; Path=$f.FullName}
    }
}
$results | Sort-Object Name | Format-Table -AutoSize
$outFile = Join-Path $root 'unused_classes.csv'
$results | Sort-Object Name | ConvertTo-Csv -NoTypeInformation | Out-File $outFile -Encoding UTF8
Write-Output "WROTE:$outFile"
