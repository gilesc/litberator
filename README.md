# litberator

Download biomedical journal PDFs by entering a standard
Pubmed query string as input.

I found existing alternatives to be either [difficult to
operate](http://code.google.com/p/pdfetch/) or [completely
nonfunctional on my platform
(linux)](http://pubget.com/help/firefox_plugin). But if there are
other alternatives that work well please let me know!

## Requirements & Installation

You will only have access to PDFs that you would normally be able to
get through your browser.  Meaning for most journals you will need to
be using the internet connection of an institution that has a subscription.

1. Ensure [Leiningen](http://www.github.com/technomancy/leiningen),
the Clojure package manager, is installed from the provided link or
your system's package manager. 

2. Ensure ~/.lein/bin/ is on your $PATH.

3. Run:

         lein install litberator

## Usage

Invoke like:

       litberator [OPTIONS] query target-directory

Options are:

* -c, Instead of downloading, just output how many PDFs would be
  downloaded with the current query.
* -n, Max number of PDFs retrieved.

Highly recommended in conjunction with the "query" accession type. By
default the -n is 50 for "query" and unlimited for the other accession types.

## Examples

        litberator -n 75 "breast cancer" .

would download the 75 most current articles with "breast cancer" in
the title, abstract, MeSH terms, etc.

litberator also supports any of the boolean keys, advanced queries,
etc, that PubMed does (see
[here](http://www.ncbi.nlm.nih.gov/books/NBK3827/) for a full
listing). For example,

          litberator "Giles CB[AU]" ~/Desktop/

would download all my papers onto your desktop.  Unfortunately, this
example wouldn't exactly fill your desktop up, but with more senior
authors it would be wise to supply a download limit with -n !

You can also see before initiating the download how many PDFs WOULD be
downloaded by using the -c flag.

           litberator -c "Wren JD[AU] relationship[TIAB]" ~/Desktop/
           >> 4

Note that this number may be smaller than the number of results you'd
get if you entered the same query into Pubmed, because litberator can
only retrieve articles with DOIs (generally meaning newer articles).
           
## Journals & Repositories Supported 

Can be found in journals.txt . If your journal of interest isn't
included, raise an issue on [my
Github](https://github.com/gilesc/litberator) 
and I'll be happy to add it!

## Future directions

* Allow more options on the output PDF file name
* Provide a little GUI or web interface

## License & Disclaimer
(C) Cory Giles 2011, GNU GPL v3.

USE OF THIS PROGRAM MAY BE AGAINST SOME JOURNALS' POLICIES. IT IS YOUR
RESPONSIBILITY TO ENSURE THE ACCESSIONS YOU ARE REQUESTING ARE ALLOWED
TO BE AUTOMATICALLY DOWNLOADED. ABOVE ALL, I WOULD CAUTION AGAINST BEING
[IDEALISTIC/STUPID](http://articles.boston.com/2011-07-20/news/29795246_1_computer-fraud-computer-security-download)
AND TRYING TO DOWNLOAD THOUSANDS OF ARTICLES AT ONCE. ALTHOUGH THE
CURRENT STATE OF ACADEMIC PUBLISHING IS LAMENTABLE, I AM NOT LIABLE
FOR ANYTHING THAT HAPPENS TO YOU OR YOUR INSTITUTION FOR USING THIS PROGRAM.


