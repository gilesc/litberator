# litberator

Download (mostly biomedical) journal PDFs using DOI, PMID, BibTeX or
Pubmed query string as input.

## Requirements & Installation

You will only have access to PDFs that you would normally be able to
get through your browser.  Meaning for most journals you will need to
be using the internet connection of an institution that has a subscription.

1. Ensure [Leiningen](http://www.github.com/technomancy/leiningen),
the Clojure package manager, is installed from the provided link or
your system's package manager. 

2. Ensure ~/.lein/bin is on your $PATH.

3. 

         lein install litberator

## Usage

Invoke like:

       litberator $ACCESSION_TYPE [FLAGS] accessions.txt target-directory

OR

       cat accessions.txt | litberator $ACCESSION_TYPE [FLAGS] target-directory

$ACCESSION_TYPE can be:

* doi 
* pmid
* bibtex
* query

Flags are:

* -n, Max number of PDFs retrieved.

Highly recommended in conjunction with the "query" accession type. By
default the -n is 50 for "query" and unlimited for the other accession types.

## Examples

        echo -e "123456\n123457\n123458" | litberator pmid /home/me/

        echo -e "10.1126/science.1133420" | litberator doi /my/directory/

The syntax for "litberator query" is slightly different:

    litberator query -n 75 "breast cancer" /my/directory/

## Journals & Repositories Supported
Can be found in journals.txt . If your journal of interest isn't
included, raise an issue and I'll be happy to add it!

## Future directions

* Accept PMIDs and other accessions instead of just DOIs
* Get accessions from a PubMed-esque query string (route thru
  e.g. http://www.bioinformatics.org/texmed/)
* Provide a little GUI or web interface

## License & Disclaimer
(C) Cory Giles 2011, GNU GPL v3.

USE OF THIS PROGRAM MAY BE AGAINST SOME JOURNALS' POLICIES. IT IS YOUR
RESPONSIBILITY TO ENSURE THE ACCESSIONS YOU ARE REQUESTING ARE ALLOWED
TO BE AUTOMATICALLY DOWNLOADED. ABOVE ALL, I WOULD CAUTION AGAINST BEING
[IDEALISTIC/STUPID](http://articles.boston.com/2011-07-20/news/29795246_1_computer-fraud-computer-security-download)
AND TRYING TO DOWNLOAD THOUSANDS OF ARTICLES AT ONCE. ALTHOUGH THE
CURRENT STATE OF ACADEMIC PUBLISHING IS LAMENTABLE, I AM NOT LIABLE
FOR ANYTHING THAT HAPPENS TO YOUR OR YOUR INSTITUTION FOR USING THIS PROGRAM.


