# include /home/ec-rdn3/guerra/X11/OI/config.mk
# OI = /sol/usr/lib/X11/OI/OI

C++=gcc

.SUFFIXES: .o .C .class .java

.C.o:
	$(C++) -g $(C++FLAGS)  $*.o $*.C
#	$(C++) $(C++FLAGS)  $*.o $*.C ; $(RM) $*..c

%:	%.o
	$(C++) -o $* $*.o $(LDFLAGS) $(LIBS) -lm

PROGRAMS =  oimove command

all: object_list.class object_node.class oimove.class property_list.class property_node.class rel_name_iter.class rel_obj_iter.class relationship_list.class relationship_node.class rubber.class selected_line.class d_tech_list.class

.java.class:
	javac -g $<

run: all
	java -Djava.compiler=NONE oimove 20 14

# all: $(PROGRAMS)

clean: 
	-@$(RM) $(PROGRAMS) *.class

clobber: clean
	-@$(RM) *.o

depend:
	$(DEPEND) -s "# DO NOT DELETE"  -- $(DEFINES) $(DEPINCLUDES) -- *.C

install:

#***************************************************************
# do not use any targets below this point, they are for
# internal use at Solbourne.
# the co target is used to get the source files
# from RCS control.
#***************************************************************
REV =
co:
	@echo checking out $(REV) source in demos/translation
		@co -q RCS/*,v
