# Scan unused_classes.csv and search repository for fully-qualified and simple-name usages
$root = 'c:\workspace\microsservico-atendimento'
$csv = Join-Path $root 'unused_classes.csv'
if (-not (Test-Path $csv)) { Write-Error "CSV not found: $csv"; exit 1 }
$allFiles = Get-ChildItem -Recurse -File -Path $root | Select-Object -ExpandProperty FullName
$results = @()
Import-Csv $csv | ForEach-Object {
    $name = $_.Name -replace '"',''
    $path = ($_.'Path' -replace '"','')
    if (-not (Test-Path $path)) { $ownFile = $null } else { $ownFile = (Get-Item $path).FullName }
    # read package line
    $pkg = ''
    if ($ownFile) {
        $lines = Get-Content $ownFile -ErrorAction SilentlyContinue
        foreach ($l in $lines) { if ($l -match '^\s*package\s+([\w\.]+)\s*;') { $pkg=$Matches[1]; break } }
    }
    $fqcn = if ($pkg) { "$pkg.$name" } else { $name }
    $patternFqcn = [regex]::Escape($fqcn)
    $patternSimple = "\b" + [regex]::Escape($name) + "\b"
    $matchesFqcn = Select-String -Path $allFiles -Pattern $patternFqcn -AllMatches -ErrorAction SilentlyContinue | Where-Object { $_.Path -ne $ownFile }
    $matchesSimple = Select-String -Path $allFiles -Pattern $patternSimple -AllMatches -ErrorAction SilentlyContinue | Where-Object { $_.Path -ne $ownFile }
    $inTarget = $matchesSimple | Where-Object { $_.Path -match '\\target\\' }
    $inTests = $matchesSimple | Where-Object { $_.Path -match '\\src\\test\\' }
    $inResources = $matchesSimple | Where-Object { $_.Path -match '\\resources\\' }
    $inNonJava = $matchesSimple | Where-Object { $_.Path -notmatch '\\.java$' }
    $countFqcn = ($matchesFqcn | Measure-Object).Count
    $countSimple = ($matchesSimple | Measure-Object).Count
    $filesFqcn = ($matchesFqcn | Select-Object -ExpandProperty Path -Unique) -join ';'
    $filesSimple = ($matchesSimple | Select-Object -ExpandProperty Path -Unique) -join ';'
    $status = 'SAFE_TO_REMOVE'
    if ($countFqcn -gt 0 -or $countSimple -gt 0 -or $inTarget.Count -gt 0 -or $inResources.Count -gt 0 -or $inNonJava.Count -gt 0 -or $inTests.Count -gt 0) { $status='POSSIBLE_USE_VIA_FRAMEWORK_OR_TESTS' }
    $results += [PSCustomObject]@{
        Name = $name
        FQCN = $fqcn
        CountFQCN = $countFqcn
        CountSimple = $countSimple
        InTarget = ($inTarget | Select-Object -ExpandProperty Path -Unique) -join ';'
        InTests = ($inTests | Select-Object -ExpandProperty Path -Unique) -join ';'
        InResources = ($inResources | Select-Object -ExpandProperty Path -Unique) -join ';'
        InNonJava = ($inNonJava | Select-Object -ExpandProperty Path -Unique) -join ';'
        FilesFQCN = $filesFqcn
        FilesSimple = $filesSimple
        Status = $status
        OwnFile = $ownFile
    }
}
$out = Join-Path $root 'unused_classes_full_report.csv'
$results | Export-Csv -Path $out -NoTypeInformation -Encoding UTF8
Write-Output "WROTE:$out"
$results | Format-Table -AutoSize
