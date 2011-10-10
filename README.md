# litberator

I wanted to download PDFs from biomedical journals to put on my
Kindle, and it's annoying to download them one at a time.  Also, it's
nice to be able to get all PDFs, say, from a certain author or in a
particular journal issue, to read all at once, perhaps when I'm off
campus and don't have access to institutional subscriptions.

## Requirements

You will only have access to PDFs that you would normally be able to
get through your browser.  Meaning for most journals you will need to
be using the internet connection of an institution that has a subscription.

## Usage

Right now, only programmatic usage:

      (def articles (read-bibtex 'your-bibtex-file.bib'))
      (doseq [article articles]
              (download-article article 
                                (str (:title article) ".pdf")))


Where the BibTeX file must have a "doi" field for each article you
want to download.  One good place to get such files is [Microsoft
Academic Search](http://academic.research.microsoft.com/).

More intuitive command-line API coming soon.

## Future directions

* Provide an API
* Accept PMIDs and other accessions instead of just DOIs
* Get accessions from a PubMed-esque query string (route thru
  e.g. http://www.bioinformatics.org/texmed/)
* Provide a little GUI or web interface


