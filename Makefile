PDFLATEX="pdflatex"
BIBTEX="bibtex"
NAME="tesi"
FILE="$NAME.tex"

.PHONY: all pdf index clean images

all: biblio index images pdf

pdf:
	${PDFLATEX} ${NAME}

index: pdf
	${PDFLATEX} ${NAME}

biblio: pdf
	${BIBTEX} ${NAME}
clean:
	rm -f *.aux
	rm -f ${NAME}.log
	rm -f ${NAME}.pdf
	rm -f ${NAME}.toc
	rm -f ${NAME}.blg
	rm -f ${NAME}.bbl