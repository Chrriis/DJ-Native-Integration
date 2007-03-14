/*
 * Issam Chehab (issam.chehab at gmail dot com)
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
#ifndef __ICON_CREATOR_H__
#define __ICON_CREATOR_H__
class IconCreator {
    static CLSID BmpCodecClsId;
    static CLSID* pBmpCodecClsId;
    static CLSID* getBmpCodec();
public:
    static HICON convertToBmp(unsigned char* content, unsigned int len, unsigned int iconWidth);
};
#endif