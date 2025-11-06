# Generate detailed per-class report with line-level matches and annotation hints
$root = 'c:\workspace\microsservico-atendimento'
$in = Join-Path $root 'unused_classes_full_report.csv'
$out = Join-Path $root 'unused_classes_detailed_report.csv'
if (-not (Test-Path $in)) { Write-Error "Input CSV not found: $in"; exit 1 }
$allFiles = Get-ChildItem -Recurse -File -Path $root | Select-Object -ExpandProperty FullName
$classes = Import-Csv $in
$results = @()
foreach ($c in $classes) {
    $name = $c.Name -replace '"',''
    $fqcn = $c.FQCN -replace '"',''
    $own = $c.OwnFile -replace '"',''

    # detect annotations / runtime hints inside the source file
    $hints = @()
    if (Test-Path $own) {
        $lines = Get-Content $own -ErrorAction SilentlyContinue
        $src = $lines -join "\n"
        if ($src -match '@RestController') { $hints += 'RestController' }
        if ($src -match '@Controller') { $hints += 'Controller' }
        if ($src -match '@Service') { $hints += 'Service' }
        if ($src -match '@Component') { $hints += 'Component' }
        if ($src -match '@Configuration') { $hints += 'Configuration' }
        if ($src -match '@Repository') { $hints += 'Repository' }
        if ($src -match '@KafkaListener') { $hints += 'KafkaListener' }
        if ($src -match '@FeignClient') { $hints += 'FeignClient' }
        if ($src -match 'implements\s+CommandLineRunner') { $hints += 'CommandLineRunner' }
        if ($src -match 'extends\s+OncePerRequestFilter') { $hints += 'OncePerRequestFilter' }
        if ($src -match '@Bean') { $hints += 'Bean' }
        if ($src -match '@Enable') { $hints += 'EnableAnnotation' }
    }
    $hintsStr = ($hints -join ';')

    # find occurrences of fully-qualified name and simple name
    $patternFq = [regex]::Escape($fqcn)
    $patternSimple = "\b" + [regex]::Escape($name) + "\b"
    $matchesFq = Select-String -Path $allFiles -Pattern $patternFq -AllMatches -ErrorAction SilentlyContinue
    $matchesSimple = Select-String -Path $allFiles -Pattern $patternSimple -AllMatches -ErrorAction SilentlyContinue

    # combine matches and mark type
    $allMatches = @()
    foreach ($m in $matchesFq) { $allMatches += [PSCustomObject]@{Path=$m.Path; LineNumber=$m.LineNumber; Line=$m.Line.Trim(); Type='FQCN'} }
    foreach ($m in $matchesSimple) { $allMatches += [PSCustomObject]@{Path=$m.Path; LineNumber=$m.LineNumber; Line=$m.Line.Trim(); Type='Simple'} }

    if ($allMatches.Count -eq 0) {
        $results += [PSCustomObject]@{
            Name=$name; FQCN=$fqcn; OwnFile=$own; AnnotationHints=$hintsStr; MatchPath=''; MatchLineNumber=''; MatchLine=''; MatchType=''; Status='NO_MATCHES_FOUND'
        }
    } else {
        foreach ($am in $allMatches | Sort-Object Path, LineNumber) {
            # skip the declaration line inside own file pointing to itself (own file detecting the class name)
            $skip = $false
            if ($own -and ($am.Path -eq $own)) {
                # allow matches in own file but skip the class declaration line containing 'public class Name' to avoid noise
                $declPattern = 'public\s+class\s+' + [regex]::Escape($name)
                if ($am.Line -match $declPattern) { $skip = $true }
            }
            if (-not $skip) {
                $status = 'POSSIBLE_USE_VIA_FRAMEWORK_OR_TESTS'
                # if match only occurs inside target jars and generated files, still possible use
                $results += [PSCustomObject]@{
                    Name=$name; FQCN=$fqcn; OwnFile=$own; AnnotationHints=$hintsStr; MatchPath=$am.Path; MatchLineNumber=$am.LineNumber; MatchLine=$am.Line; MatchType=$am.Type; Status=$status
                }
            }
        }
    }
}
# export
$results | Export-Csv -Path $out -NoTypeInformation -Encoding UTF8
Write-Output "WROTE:$out"
# print summary counts
$summary = $results | Group-Object Name | Select-Object Name,@{Name='Matches';Expression={$_.Count}}
Write-Output "Summary (name : matches)"
$summary | ForEach-Object { Write-Output ("$($_.Name) : $($_.Matches)") }
