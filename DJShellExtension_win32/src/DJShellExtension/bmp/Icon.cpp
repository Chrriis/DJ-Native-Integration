/*
 * Issam Chehab (issam.chehab at gmail dot com)
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
#include "stdafx.h"
#include "Icons.h"
#include "Dib.H"


/****************************************************************************/
// Structs used locally (file scope)
// Resource Position info - size and offset of a resource in a file
typedef struct
{
    DWORD	dwBytes;
    DWORD	dwOffset;
} RESOURCEPOSINFO, *LPRESOURCEPOSINFO;
// EXE/DLL icon information - filename, instance handle and ID
typedef struct
{
    LPCTSTR    	szFileName;
    HINSTANCE	hInstance;
    LPTSTR    	lpID;
} EXEDLLICONINFO, *LPEXEDLLICONINFO;
/****************************************************************************/


/****************************************************************************/

/****************************************************************************/

/****************************************************************************/
// Prototypes for local functions
BOOL AdjustIconImagePointers( LPICONIMAGE lpImage );


DWORD CalculateImageOffset( LPICONRESOURCE lpIR, UINT nIndex );
BOOL DIBToIconImage( LPICONIMAGE lpii, LPBYTE lpDIB, BOOL bStretch );
/****************************************************************************/

HICON MakeIconFromResource( LPICONIMAGE lpIcon )
{
    HICON        	hIcon = NULL;

    // Sanity Check
    if( lpIcon == NULL )
        return NULL;
    if( lpIcon->lpBits == NULL )
        return NULL;
    // Let the OS do the real work :)
    hIcon = CreateIconFromResourceEx( lpIcon->lpBits, lpIcon->dwNumBytes, TRUE, 0x00030000, 
            (*(LPBITMAPINFOHEADER)(lpIcon->lpBits)).biWidth, (*(LPBITMAPINFOHEADER)(lpIcon->lpBits)).biHeight/2, 0 );
    
    // It failed, odds are good we're on NT so try the non-Ex way
    if( hIcon == NULL )
    {
        // We would break on NT if we try with a 16bpp image
        if(lpIcon->lpbi->bmiHeader.biBitCount != 16)
        {	
            hIcon = CreateIconFromResource( lpIcon->lpBits, lpIcon->dwNumBytes, TRUE, 0x00030000 );
        }
    }
    return hIcon;
}

BOOL AdjustIconImagePointers( LPICONIMAGE lpImage )
{
    // Sanity check
    if( lpImage==NULL )
        return FALSE;
    // BITMAPINFO is at beginning of bits
    lpImage->lpbi = (LPBITMAPINFO)lpImage->lpBits;
    // Width - simple enough
    lpImage->Width = lpImage->lpbi->bmiHeader.biWidth;
    // Icons are stored in funky format where height is doubled - account for it
    lpImage->Height = (lpImage->lpbi->bmiHeader.biHeight)/2;
    // How many colors?
    lpImage->Colors = lpImage->lpbi->bmiHeader.biPlanes * lpImage->lpbi->bmiHeader.biBitCount;
    // XOR bits follow the header and color table
    lpImage->lpXOR = (LPBYTE)FindDIBBits((LPSTR)lpImage->lpbi);
    // AND bits follow the XOR bits
    lpImage->lpAND = lpImage->lpXOR + (lpImage->Height*BytesPerLine((LPBITMAPINFOHEADER)(lpImage->lpbi)));
    return TRUE;
}







DWORD CalculateImageOffset( LPICONRESOURCE lpIR, UINT nIndex )
{
    DWORD	dwSize;
    UINT    i;

    // Calculate the ICO header size
    dwSize = 3 * sizeof(WORD);
    // Add the ICONDIRENTRY's
    dwSize += lpIR->nNumImages * sizeof(ICONDIRENTRY);
    // Add the sizes of the previous images
    for(i=0;i<nIndex;i++)
        dwSize += lpIR->IconImages[i].dwNumBytes;
    // we're there - return the number
    return dwSize;
}


BOOL DIBToIconImage( LPICONIMAGE lpii, LPBYTE lpDIB, BOOL bStretch )
{
    LPBYTE    	lpNewDIB;

    // Sanity check
    if( lpDIB == NULL )
        return FALSE;

    // Let the DIB engine convert color depths if need be
    lpNewDIB = ConvertDIBFormat( (LPBITMAPINFO)lpDIB, lpii->Width, lpii->Height, lpii->Colors, bStretch );

    // Now we have a cool new DIB of the proper size/color depth
    // Lets poke it into our data structures and be done with it

    // How big is it?
    lpii->dwNumBytes = sizeof( BITMAPINFOHEADER )                    	// Header
                    + PaletteSize( (LPSTR)lpNewDIB )                    // Palette
                    + lpii->Height * BytesPerLine( (LPBITMAPINFOHEADER)lpNewDIB )	// XOR mask
                    + lpii->Height * WIDTHBYTES( lpii->Width );        	// AND mask

    // If there was already an image here, free it
    if( lpii->lpBits != NULL )
        free( lpii->lpBits );
    // Allocate enough room for the new image
    if( (lpii->lpBits = (LPBYTE)malloc( lpii->dwNumBytes )) == NULL )
    {
        free( lpii );
        return FALSE;
    }
    // Copy the bits
    memcpy( lpii->lpBits, lpNewDIB, sizeof( BITMAPINFOHEADER ) + PaletteSize( (LPSTR)lpNewDIB ) );
    // Adjust internal pointers/variables for new image
    lpii->lpbi = (LPBITMAPINFO)(lpii->lpBits);
    lpii->lpbi->bmiHeader.biHeight *= 2;
    lpii->lpXOR = (LPBYTE)FindDIBBits( (LPSTR)(lpii->lpBits) );
    memcpy( lpii->lpXOR, FindDIBBits((LPSTR)lpNewDIB), lpii->Height * BytesPerLine( (LPBITMAPINFOHEADER)lpNewDIB ) );
    lpii->lpAND = lpii->lpXOR + lpii->Height * BytesPerLine( (LPBITMAPINFOHEADER)lpNewDIB );
    memset( lpii->lpAND, 0, lpii->Height * WIDTHBYTES( lpii->Width ) );
    // Free the source
    free( lpNewDIB );
    return TRUE;
}

BOOL CreateBlankNewFormatIcon( LPICONIMAGE lpii )
{
    DWORD            	dwFinalSize;
    BITMAPINFOHEADER    bmih;

    // Fill in the bitmap header
    ZeroMemory( &bmih, sizeof( BITMAPINFOHEADER ) );
    bmih.biSize = sizeof( BITMAPINFOHEADER );
    bmih.biBitCount = lpii->Colors;
    bmih.biClrUsed = 0;
    
    // How big will the final thing be?
    // Well, it'll have a header
    dwFinalSize = sizeof( BITMAPINFOHEADER );
    // and a color table (even if it's zero length)
    dwFinalSize += PaletteSize( (LPSTR)&bmih );
    // and XOR bits
    dwFinalSize += lpii->Height * WIDTHBYTES( lpii->Width * lpii->Colors );
    // and AND bits. That's about it :)
    dwFinalSize += lpii->Height * WIDTHBYTES( lpii->Width );

    // Allocate some memory for it
    lpii->lpBits = (LPBYTE)malloc( dwFinalSize );
    ZeroMemory( lpii->lpBits, dwFinalSize );
    lpii->dwNumBytes = dwFinalSize;
    lpii->lpbi = (LPBITMAPINFO)(lpii->lpBits);
    lpii->lpXOR = (LPBYTE)(lpii->lpbi) + sizeof(BITMAPINFOHEADER) + PaletteSize( (LPSTR)&bmih );
    lpii->lpAND = lpii->lpXOR + (lpii->Height * WIDTHBYTES( lpii->Width * lpii->Colors ));

    // The bitmap header is zeros, fill it out
    lpii->lpbi->bmiHeader.biSize = sizeof( BITMAPINFOHEADER ); 
    lpii->lpbi->bmiHeader.biWidth = lpii->Width;
    // Don't forget the funky height*2 icon resource thing
    lpii->lpbi->bmiHeader.biHeight = lpii->Height * 2; 
    lpii->lpbi->bmiHeader.biPlanes = 1; 
    lpii->lpbi->bmiHeader.biBitCount = lpii->Colors; 
    lpii->lpbi->bmiHeader.biCompression = BI_RGB; 
                   
    return TRUE;
}
RECT GetXORImageRect( RECT Rect, LPICONIMAGE lpIcon )
{
    RECT    NewRect;

    // Just center the thing in the bounding display rect
    NewRect.left = Rect.left + ((RectWidth(Rect)-lpIcon->lpbi->bmiHeader.biWidth)/2);
    NewRect.top = Rect.top + ((RectHeight(Rect)-(lpIcon->lpbi->bmiHeader.biHeight/2))/2);
    NewRect.bottom = NewRect.top + (lpIcon->lpbi->bmiHeader.biHeight/2);
    NewRect.right = NewRect.left + lpIcon->lpbi->bmiHeader.biWidth;
    return NewRect;
}


BOOL DrawXORMask( HDC hDC, RECT Rect, LPICONIMAGE lpIcon )
{
    int            	x, y;

    // Sanity checks
    if( lpIcon == NULL )
        return FALSE;
    if( lpIcon->lpBits == NULL )
        return FALSE;

    // Account for height*2 thing
    lpIcon->lpbi->bmiHeader.biHeight /= 2;

    // Locate it
    x = Rect.left + ((RectWidth(Rect)-lpIcon->lpbi->bmiHeader.biWidth)/2);
    y = Rect.top + ((RectHeight(Rect)-lpIcon->lpbi->bmiHeader.biHeight)/2);

    // Blast it to the screen
    SetDIBitsToDevice( hDC, x, y, lpIcon->lpbi->bmiHeader.biWidth, lpIcon->lpbi->bmiHeader.biHeight, 0, 0, 0, lpIcon->lpbi->bmiHeader.biHeight, lpIcon->lpXOR, lpIcon->lpbi, DIB_RGB_COLORS );

    // UnAccount for height*2 thing
    lpIcon->lpbi->bmiHeader.biHeight *= 2;

    return TRUE;
}


BOOL DrawANDMask( HDC hDC, RECT Rect, LPICONIMAGE lpIcon )
{
    LPBITMAPINFO    lpbi;
    int            	x, y;

    // Sanity checks
    if( lpIcon == NULL )
        return FALSE;
    if( lpIcon->lpBits == NULL )
        return FALSE;

    // Need a bitmap header for the mono mask
    lpbi = (LPBITMAPINFO)malloc( sizeof(BITMAPINFO) + (2 * sizeof( RGBQUAD )) );
    lpbi->bmiHeader.biSize = sizeof( BITMAPINFOHEADER );
    lpbi->bmiHeader.biWidth = lpIcon->lpbi->bmiHeader.biWidth;
    lpbi->bmiHeader.biHeight = lpIcon->lpbi->bmiHeader.biHeight/2;
    lpbi->bmiHeader.biPlanes = 1;
    lpbi->bmiHeader.biBitCount = 1;
    lpbi->bmiHeader.biCompression = BI_RGB;
    lpbi->bmiHeader.biSizeImage = 0;
    lpbi->bmiHeader.biXPelsPerMeter = 0;
    lpbi->bmiHeader.biYPelsPerMeter = 0;
    lpbi->bmiHeader.biClrUsed = 0;
    lpbi->bmiHeader.biClrImportant = 0;
    lpbi->bmiColors[0].rgbRed = 0;
    lpbi->bmiColors[0].rgbGreen = 0;
    lpbi->bmiColors[0].rgbBlue = 0;
    lpbi->bmiColors[0].rgbReserved = 0;
    lpbi->bmiColors[1].rgbRed = 255;
    lpbi->bmiColors[1].rgbGreen = 255;
    lpbi->bmiColors[1].rgbBlue = 255;
    lpbi->bmiColors[1].rgbReserved = 0;

    // Locate it
    x = Rect.left + ((RectWidth(Rect)-lpbi->bmiHeader.biWidth)/2);
    y = Rect.top + ((RectHeight(Rect)-lpbi->bmiHeader.biHeight)/2);

    // Blast it to the screen
    SetDIBitsToDevice( hDC, x, y, lpbi->bmiHeader.biWidth, lpbi->bmiHeader.biHeight, 0, 0, 0, lpbi->bmiHeader.biHeight, lpIcon->lpAND, lpbi, DIB_RGB_COLORS );

    // clean up
    free( lpbi );

    return TRUE;
}
BOOL MakeNewANDMaskBasedOnPoint( LPICONIMAGE lpIcon, POINT pt )
{
    HBITMAP        	hXORBitmap, hOldXORBitmap;
    HDC            	hDC, hMemDC1;
    LPBYTE        	pXORBits;
    COLORREF        crTransparentColor;
    LONG            i,j;


    // Account for height*2 thing
    lpIcon->lpbi->bmiHeader.biHeight /= 2;

    // Need a DC
    hDC = GetDC( NULL );

    // Use DIBSection for source
    hXORBitmap = CreateDIBSection( hDC, lpIcon->lpbi, DIB_RGB_COLORS, (void**)&pXORBits, NULL, 0  );
    memcpy( pXORBits, lpIcon->lpXOR, (lpIcon->lpbi->bmiHeader.biHeight) * BytesPerLine((LPBITMAPINFOHEADER)(lpIcon->lpbi)) );
    hMemDC1 = CreateCompatibleDC( hDC );
    hOldXORBitmap = (HBITMAP)SelectObject( hMemDC1, hXORBitmap );

    // Set the color table if need be
    if( lpIcon->lpbi->bmiHeader.biBitCount <= 8 )
        SetDIBColorTable( hMemDC1, 0, DIBNumColors((LPSTR)(lpIcon->lpbi)), lpIcon->lpbi->bmiColors);
    
    // What's the transparent color?
    crTransparentColor = GetPixel( hMemDC1, pt.x, pt.y );

    // Loop through the pixels
    for(i=0;i<lpIcon->lpbi->bmiHeader.biWidth;i++)
    {
        for(j=0;j<lpIcon->lpbi->bmiHeader.biHeight;j++)
        {
            // Is the source transparent at This point?
            if( GetPixel( hMemDC1, i, j ) == crTransparentColor )
            {
                // Yes, so set the pixel in AND mask, and clear it in XOR mask
                SetMonoDIBPixel( lpIcon->lpAND, lpIcon->lpbi->bmiHeader.biWidth, lpIcon->lpbi->bmiHeader.biHeight, i, j, TRUE );     
                if( lpIcon->lpbi->bmiHeader.biBitCount == 1 )
                    SetMonoDIBPixel( pXORBits, lpIcon->lpbi->bmiHeader.biWidth, lpIcon->lpbi->bmiHeader.biHeight, i, j, FALSE );     
                else
                    SetPixelV( hMemDC1, i, j, RGB(0,0,0) );
            }
            else
            {
                // No, so clear pixel in AND mask
                SetMonoDIBPixel( lpIcon->lpAND, lpIcon->lpbi->bmiHeader.biWidth, lpIcon->lpbi->bmiHeader.biHeight, i, j, FALSE );    
            }
        }
    }
    // Flush the SetPixelV() calls
    GdiFlush();

    SelectObject( hMemDC1, hOldXORBitmap );

    // Copy the new XOR bits back to our storage
    memcpy( lpIcon->lpXOR, pXORBits, (lpIcon->lpbi->bmiHeader.biHeight) * BytesPerLine((LPBITMAPINFOHEADER)(lpIcon->lpbi)) );

    // Clean up
    DeleteObject( hXORBitmap );
    DeleteDC( hMemDC1 );
    ReleaseDC( NULL, hDC );


    // UnAccount for height*2 thing
    lpIcon->lpbi->bmiHeader.biHeight *= 2;
    return TRUE;
}
BOOL IconImageFromBMPFile( IStream* stream, LPICONIMAGE lpii, BOOL bStretchToFit )
{
    LPBYTE        	lpDIB = NULL;
    BOOL            bRet = FALSE;

    if( (lpDIB=ReadBMPFile(stream)) == NULL )
        return FALSE;
    // Convert it to an icon image
    bRet = DIBToIconImage( lpii, lpDIB, bStretchToFit );
    free( lpDIB );
    return bRet;
}

