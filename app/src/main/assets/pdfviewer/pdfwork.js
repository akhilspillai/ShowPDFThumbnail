var url = '/storage/sdcard/file1.pdf';

      PDFJS.getDocument(url).then(function (pdfDoc_) {
        pdfDoc = pdfDoc_;
        renderPage(1);
      });
      // Disable workers to avoid yet another cross-origin issue (workers need
      // the URL of the script to be loaded, and dynamically loading a cross-origin
      // script does not work).
      //
      //       PDFJS.disableWorker = true;
      //
      // In cases when the pdf.worker.js is located at the different folder than the
      // pdf.js's one, or the pdf.js is executed via eval(), the workerSrc property
      // shall be specified.
      //
      // PDFJS.workerSrc = '../../build/pdf.worker.js';
      var pdfDoc = null,
          pageNum = 1,
          pageRendering = false,
          pageNumPending = null,
          scale = 1,
          canvas = document.getElementById('the-canvas'),
          ctx = canvas.getContext('2d');
      /**
       * Get page info from document, resize canvas accordingly, and render page.
       * @param num Page number.
       */
      function renderPage(num) {

        pageRendering = true;
        // Using promise to fetch the page
        pdfDoc.getPage(num).then(function(page) {
          var viewport = page.getViewport(scale);
          canvas.height = viewport.height;
          canvas.width = viewport.width;
          // Render PDF page into canvas context
          var renderContext = {
            canvasContext: ctx,
            viewport: viewport
          };
          var renderTask = page.render(renderContext);
          // Wait for rendering to finish
          renderTask.promise.then(function () {

            injectedObject.rendered();
            pageRendering = false;
            if (pageNumPending !== null) {
              // New page rendering is pending
              renderPage(pageNumPending);
              pageNumPending = null;
            }
          });
        });
      }


