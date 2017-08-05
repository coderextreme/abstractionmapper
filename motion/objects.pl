#!/usr/bin/perl

$oldobj = "";
while (<>) {
	chomp;
	@obj = split(/\t/);
	if ($oldobj ne $obj[0]) {
		if ($oldobj ne "") {
			print "/>\n";
		}
		print "<object id='$obj[0]'";
	}
	print " $obj[1]='$obj[2]'";
	$oldobj = $obj[0];
}
print "/>\n";
