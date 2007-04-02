

/* this ALWAYS GENERATED file contains the IIDs and CLSIDs */

/* link this file in with the server and any clients */


 /* File created by MIDL compiler version 6.00.0366 */
/* at Mon Apr 02 21:51:24 2007
 */
/* Compiler settings for .\DesktopJavaShellExtension.idl:
    Oicf, W1, Zp8, env=Win32 (32b run)
    protocol : dce , ms_ext, c_ext
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
//@@MIDL_FILE_HEADING(  )

#pragma warning( disable: 4049 )  /* more than 64k source lines */


#ifdef __cplusplus
extern "C"{
#endif 


#include <rpc.h>
#include <rpcndr.h>

#ifdef _MIDL_USE_GUIDDEF_

#ifndef INITGUID
#define INITGUID
#include <guiddef.h>
#undef INITGUID
#else
#include <guiddef.h>
#endif

#define MIDL_DEFINE_GUID(type,name,l,w1,w2,b1,b2,b3,b4,b5,b6,b7,b8) \
        DEFINE_GUID(name,l,w1,w2,b1,b2,b3,b4,b5,b6,b7,b8)

#else // !_MIDL_USE_GUIDDEF_

#ifndef __IID_DEFINED__
#define __IID_DEFINED__

typedef struct _IID
{
    unsigned long x;
    unsigned short s1;
    unsigned short s2;
    unsigned char  c[8];
} IID;

#endif // __IID_DEFINED__

#ifndef CLSID_DEFINED
#define CLSID_DEFINED
typedef IID CLSID;
#endif // CLSID_DEFINED

#define MIDL_DEFINE_GUID(type,name,l,w1,w2,b1,b2,b3,b4,b5,b6,b7,b8) \
        const type name = {l,w1,w2,{b1,b2,b3,b4,b5,b6,b7,b8}}

#endif !_MIDL_USE_GUIDDEF_

MIDL_DEFINE_GUID(IID, IID_IIconHandler,0xD3C84433,0xAA02,0x408D,0x8E,0x25,0x5A,0xCC,0x78,0xD5,0xC5,0x30);


MIDL_DEFINE_GUID(IID, IID_IPropertySheetHandler,0x86C3A15B,0xB302,0x4B5C,0xB6,0x98,0x3D,0x9F,0x71,0x2F,0xA2,0x4E);


MIDL_DEFINE_GUID(IID, LIBID_DesktopJavaShellExtensionLib,0x3A1ABF8B,0xEBA1,0x4DC8,0xA9,0xA8,0x43,0x71,0xF3,0xB5,0x99,0xF8);


MIDL_DEFINE_GUID(CLSID, CLSID_IconHandler,0x560F06E8,0xE0EF,0x4DD6,0x97,0x33,0x5C,0x25,0x07,0x13,0x2C,0x57);


MIDL_DEFINE_GUID(CLSID, CLSID_PropertySheetHandler,0x5EC050B4,0x7064,0x44B9,0x8D,0x71,0xEC,0x1A,0x33,0xDF,0xB6,0x04);

#undef MIDL_DEFINE_GUID

#ifdef __cplusplus
}
#endif



