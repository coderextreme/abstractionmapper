#!/usr/bin/perl

while (<STDIN>) {
	s/\&gt;/>/;
	s/\&lt;/</;
	s/<br>/ /g;
	s/<font[^>]*>/-->/;
	s/<\/font>//;
	print;
}
