#!/usr/bin/perl

$| = 1;
`stty erase `;

do {
	print STDOUT "Nick (numbers and letters only please): ";
	$nick = <STDIN>;
	chomp $nick;
	$nick =~ s/[ 	\n\r\t]//g;
} while ($nick =~ /[^A-Za-z0-9]/ || $nick =~ /^$/);
print STDOUT "Use /quit to exit,  and backspace to back up a character. Thank you!\n";
#  $ENV{DISPLAY}="foobar:0";
$ENV{CLASSPATH}="/home/carlsonj/apps/dev";
chdir("/home/carlsonj/apps/dev");
system("/cygdrive/c/WINDOWS/system32/java -jar E:/cygwin/home/carlsonj/apps/dev/icbmc.jar | /home/carlsonj/apps/dev/striphtml.pl");

