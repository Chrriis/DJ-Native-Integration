/****************************************************************************\
*			 
*     FILE:     DIB.H
*
*     PURPOSE:  IconPro Project DIB handling header file
*
*     COMMENTS: Icons are stored in something almost identical to DIB
*               format, which makes it real easy to treat them as DIBs
*               when manipulating them.
*
*     Copyright 1995 Microsoft Corp.
*
*
* History:
*                July '95 - Created
*
\****************************************************************************/

// How large of icons will we support? This is really only important
// bacuase of the design of the UI. It needs to draw the entire icon
// 4 times, so we limit how big one can be. The ICO spec puts no limit
// on icon sizes.
#define MAX_ICON_WIDTH	128              // Max width
#define MIN_ICON_WIDTH	16               // Min width
#define MAX_ICON_HEIGHT	MAX_ICON_WIDTH   // Max height
#define MIN_ICON_HEIGHT	MIN_ICON_WIDTH   // Min height

// How big do the MDI child windows need to be to display the icon and
// the listbox?
#define WINDOW_WIDTH		( ( MAX_ICON_WIDTH * 2 ) + 30 )
#define WINDOW_HEIGHT		( ( MAX_ICON_HEIGHT * 2 ) + 150 )

// Utility macro to calculate a rectangle's width/height
#define RectWidth(r)		((r).right - (r).left + 1)
#define RectHeight(r)		((r).bottom - (r).top + 1)
/****************************************************************************/




/****************************************************************************/
// Exported function prototypes
BOOL GetSaveIconFileName( LPTSTR szFileName, UINT FilterStringID, LPCTSTR szTitle );
BOOL GetOpenIconFileName( LPTSTR szFileName, UINT FilterStringID, LPCTSTR szTitle );
/****************************************************************************/

/****************************************************************************/
// local #defines

// How wide, in bytes, would This many bits be, DWORD aligned?
#define WIDTHBYTES(bits)      ((((bits) + 31)>>5)<<2)
/****************************************************************************/


/****************************************************************************/
// Exported function prototypes
LPSTR FindDIBBits (LPSTR lpbi);
WORD DIBNumColors (LPSTR lpbi);
WORD PaletteSize (LPSTR lpbi);
DWORD BytesPerLine( LPBITMAPINFOHEADER lpBMIH );
LPBYTE ConvertDIBFormat( LPBITMAPINFO lpSrcDIB, UINT nWidth, UINT nHeight, UINT nColors, BOOL bStretch );
void SetMonoDIBPixel( LPBYTE pANDBits, DWORD dwWidth, DWORD dwHeight, DWORD x, DWORD y, BOOL bWhite );
LPBYTE ReadBMPFile( IStream * szFileName );

/****************************************************************************/
