# GNA-Practicum-3
This is an image processor that stitches two images together. It does this by looking at the RGB values and determining the most cost-efficient route from the upper left to the bottom right corner.
## Installing
To install, download the directory and install apache ant. A quick Google search and "Running" will help you further.
## Running
You can run this project by using *Apache Ant 1.9.6*, this version was used for testing.
To run this on two images, you have to use following command:
```
ant run -Dimg1=PATH_TO_FILE/img1.png -Dimg2=PATH_TO_FILE/img2.png
```
I suggest you use **.png** or **.jpg** files, as I'm not sure what other file types you can use. Following arguments are also possible:
```
-Doffsetx=X_OFFSET -Doffsety=Y_OFFSET
```
These offsets affect the second image. For example: if *offsety* is negative, the second image will be moved upward, and if *offsetx* is positive, the second image will be moved to the right.

## Authors
* **Professors@KULeuven** - *initial work*
* **Wh1spr** - *Making it do what it's supposed to do* - [Wh1spr](https://github.com/Wh1spr)
## DISCLAIMER
I do **not** condone any form of plagiarism. If you use this project to make the same assignment you are putting yourself at risk.
