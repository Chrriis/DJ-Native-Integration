/*
 * Issam Chehab (issam.chehab at gmail dot com)
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
// IconHandler.cpp : Implementation of CIconHandler

#include "stdafx.h"
#include "IconHandler.h"
#include "gdiplus.h"

STDMETHODIMP CIconHandler::GetClassID( CLSID* p1 )       
{ 
    return E_NOTIMPL; 
}
STDMETHODIMP CIconHandler::IsDirty()
{ 
    return E_NOTIMPL; 
}
STDMETHODIMP CIconHandler::Save( LPCOLESTR p1, BOOL p2)
{ 
    return E_NOTIMPL; 
}
STDMETHODIMP CIconHandler::SaveCompleted( LPCOLESTR p1) 
{ 
    return E_NOTIMPL; 
}
STDMETHODIMP CIconHandler::GetCurFile( LPOLESTR* p1)    
{ 
    return E_NOTIMPL;
}


STDMETHODIMP CIconHandler::Load(LPCOLESTR wszFile, DWORD /*dwMode*/ )
{ 

    std::wstring wstr(wszFile);
    std::string nstr(wstr.begin(),wstr.end());

    jarReader = new JarReader(wszFile);

    return S_OK;
}

STDMETHODIMP CIconHandler::GetIconLocation (
  UINT uFlags, LPTSTR szIconFile, UINT cchMax,
  int* piIndex, UINT* pwFlags )
{
    *pwFlags = GIL_DONTCACHE|GIL_NOTFILENAME|GIL_SIMULATEDOC;
     return S_OK;
}
char arry[1024*1024];
STDMETHODIMP CIconHandler::Extract(LPCTSTR pszFile, UINT nIconIndex, HICON* phiconLarge,
                       HICON* phiconSmall, UINT nIconSize)
{


    int smallSize = HIWORD(nIconSize);
    int largeSize = LOWORD(nIconSize);
    if (jarReader) {
        Gdiplus::GdiplusStartupInput input;
        ULONG_PTR token;
   
        Gdiplus::Status status = Gdiplus::GdiplusStartup(&token,&input,NULL);
        if (status != Gdiplus::Ok) {
            return S_FALSE;
        }
        bool result = jarReader->getIcons(smallSize,phiconSmall,largeSize,phiconLarge);
        Gdiplus::GdiplusShutdown(token);
        if (result) {            
            return NOERROR;
        } else {
            return S_FALSE;
        }
    } else {
        return S_FALSE;
    }
}
