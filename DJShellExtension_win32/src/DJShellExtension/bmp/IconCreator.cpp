/*
 * Issam Chehab (issam.chehab at gmail dot com)
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
#include "stdafx.h"
#include <gdiplus.h>
#include "icons.h"
#include <string>

#include "IconCreator.h"

CLSID IconCreator::BmpCodecClsId;
CLSID* IconCreator::pBmpCodecClsId;

CLSID* IconCreator::getBmpCodec()
{
    if (pBmpCodecClsId) {
        return pBmpCodecClsId;
    }

    UINT  num;
    UINT  size;
    Gdiplus::Status status ;

    status = Gdiplus::GetImageEncodersSize(&num, &size);
    if (status != Gdiplus::Ok) {
        return pBmpCodecClsId;
    }
    Gdiplus::ImageCodecInfo* pImageCodecInfo = (Gdiplus::ImageCodecInfo*)(malloc(size));
    status = Gdiplus::GetImageEncoders(num, size, pImageCodecInfo);
    if (status != Gdiplus::Ok) {
        free(pImageCodecInfo);
        return pBmpCodecClsId;
    }
    for(UINT j = 0; j < num; ++j)
    { 
        if (std::wstring(L"image/bmp") == std::wstring(pImageCodecInfo[j].MimeType)) {
            BmpCodecClsId = pImageCodecInfo[j].Clsid;
            pBmpCodecClsId = &BmpCodecClsId;
            break;
        }      
    }
    free(pImageCodecInfo);
    return pBmpCodecClsId;
}
HICON IconCreator::convertToBmp(unsigned char* content, unsigned int len, unsigned int iconWidth)
{
    if (getBmpCodec() == NULL) {
        return NULL;     
    }

    // allocate memory inorder to create an IStream
    HGLOBAL contentHandle = GlobalAlloc(GMEM_MOVEABLE|GMEM_NODISCARD,len);
    if (!content) {
        return NULL;
    }
    LPVOID vp = GlobalLock(contentHandle);
    if (!vp) {
        GlobalFree(contentHandle);
        return NULL;
    }
    memcpy(vp,content,len);
    IStream* instream = NULL;
    IStream* outstream = NULL;
    HICON hIcon = NULL;
    HRESULT  res;
    res = CreateStreamOnHGlobal(contentHandle,true,&instream);
    res = CreateStreamOnHGlobal(NULL,true,&outstream);
    {
        Gdiplus::Image img(instream); 
        res = img.Save(outstream,&BmpCodecClsId);
        STATSTG statstg;
        res = outstream->Stat(&statstg,0);
        LARGE_INTEGER li;
        li.QuadPart = 0LL;
        res = outstream->Seek(li,STREAM_SEEK_SET,NULL);
        ICONIMAGE iconimg; 
        memset(&iconimg,0,sizeof(iconimg));
        iconimg.Height = iconimg.Width = iconWidth;
        iconimg.Colors = 32;
        if (IconImageFromBMPFile(outstream,&iconimg,TRUE)) {
            hIcon = MakeIconFromResource(&iconimg);
            // we no longer need the image, free it
            if (iconimg.lpBits) {
                free(iconimg.lpBits);
            }
        }
    }
    ULONG refcount = instream->Release();
    refcount = outstream->Release();
    return hIcon;
}