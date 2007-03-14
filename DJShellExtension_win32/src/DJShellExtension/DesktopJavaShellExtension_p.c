

/* this ALWAYS GENERATED file contains the proxy stub code */


 /* File created by MIDL compiler version 7.00.0499 */
/* at Wed Mar 14 10:43:12 2007
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

#if !defined(_M_IA64) && !defined(_M_AMD64)


#pragma warning( disable: 4049 )  /* more than 64k source lines */
#if _MSC_VER >= 1200
#pragma warning(push)
#endif

#pragma warning( disable: 4211 )  /* redefine extern to static */
#pragma warning( disable: 4232 )  /* dllimport identity*/
#pragma warning( disable: 4024 )  /* array to pointer mapping*/
#pragma warning( disable: 4152 )  /* function/data pointer conversion in expression */
#pragma warning( disable: 4100 ) /* unreferenced arguments in x86 call */

#pragma optimize("", off ) 

#define USE_STUBLESS_PROXY


/* verify that the <rpcproxy.h> version is high enough to compile this file*/
#ifndef __REDQ_RPCPROXY_H_VERSION__
#define __REQUIRED_RPCPROXY_H_VERSION__ 440
#endif


#include "rpcproxy.h"
#ifndef __RPCPROXY_H_VERSION__
#error this stub requires an updated version of <rpcproxy.h>
#endif // __RPCPROXY_H_VERSION__


#include "DesktopJavaShellExtension.h"

#define TYPE_FORMAT_STRING_SIZE   3                                 
#define PROC_FORMAT_STRING_SIZE   1                                 
#define EXPR_FORMAT_STRING_SIZE   1                                 
#define TRANSMIT_AS_TABLE_SIZE    0            
#define WIRE_MARSHAL_TABLE_SIZE   0            

typedef struct _DesktopJavaShellExtension_MIDL_TYPE_FORMAT_STRING
    {
    short          Pad;
    unsigned char  Format[ TYPE_FORMAT_STRING_SIZE ];
    } DesktopJavaShellExtension_MIDL_TYPE_FORMAT_STRING;

typedef struct _DesktopJavaShellExtension_MIDL_PROC_FORMAT_STRING
    {
    short          Pad;
    unsigned char  Format[ PROC_FORMAT_STRING_SIZE ];
    } DesktopJavaShellExtension_MIDL_PROC_FORMAT_STRING;

typedef struct _DesktopJavaShellExtension_MIDL_EXPR_FORMAT_STRING
    {
    long          Pad;
    unsigned char  Format[ EXPR_FORMAT_STRING_SIZE ];
    } DesktopJavaShellExtension_MIDL_EXPR_FORMAT_STRING;


static RPC_SYNTAX_IDENTIFIER  _RpcTransferSyntax = 
{{0x8A885D04,0x1CEB,0x11C9,{0x9F,0xE8,0x08,0x00,0x2B,0x10,0x48,0x60}},{2,0}};


extern const DesktopJavaShellExtension_MIDL_TYPE_FORMAT_STRING DesktopJavaShellExtension__MIDL_TypeFormatString;
extern const DesktopJavaShellExtension_MIDL_PROC_FORMAT_STRING DesktopJavaShellExtension__MIDL_ProcFormatString;
extern const DesktopJavaShellExtension_MIDL_EXPR_FORMAT_STRING DesktopJavaShellExtension__MIDL_ExprFormatString;


extern const MIDL_STUB_DESC Object_StubDesc;


extern const MIDL_SERVER_INFO IIconHandler_ServerInfo;
extern const MIDL_STUBLESS_PROXY_INFO IIconHandler_ProxyInfo;


extern const MIDL_STUB_DESC Object_StubDesc;


extern const MIDL_SERVER_INFO IPropertySheetHandler_ServerInfo;
extern const MIDL_STUBLESS_PROXY_INFO IPropertySheetHandler_ProxyInfo;



#if !defined(__RPC_WIN32__)
#error  Invalid build platform for this stub.
#endif

#if !(TARGET_IS_NT40_OR_LATER)
#error You need a Windows NT 4.0 or later to run this stub because it uses these features:
#error   -Oif or -Oicf.
#error However, your C/C++ compilation flags indicate you intend to run this app on earlier systems.
#error This app will fail with the RPC_X_WRONG_STUB_VERSION error.
#endif


static const DesktopJavaShellExtension_MIDL_PROC_FORMAT_STRING DesktopJavaShellExtension__MIDL_ProcFormatString =
    {
        0,
        {

			0x0
        }
    };

static const DesktopJavaShellExtension_MIDL_TYPE_FORMAT_STRING DesktopJavaShellExtension__MIDL_TypeFormatString =
    {
        0,
        {
			NdrFcShort( 0x0 ),	/* 0 */

			0x0
        }
    };


/* Object interface: IUnknown, ver. 0.0,
   GUID={0x00000000,0x0000,0x0000,{0xC0,0x00,0x00,0x00,0x00,0x00,0x00,0x46}} */


/* Object interface: IDispatch, ver. 0.0,
   GUID={0x00020400,0x0000,0x0000,{0xC0,0x00,0x00,0x00,0x00,0x00,0x00,0x46}} */


/* Object interface: IIconHandler, ver. 0.0,
   GUID={0xD3C84433,0xAA02,0x408D,{0x8E,0x25,0x5A,0xCC,0x78,0xD5,0xC5,0x30}} */

#pragma code_seg(".orpc")
static const unsigned short IIconHandler_FormatStringOffsetTable[] =
    {
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    0
    };

static const MIDL_STUBLESS_PROXY_INFO IIconHandler_ProxyInfo =
    {
    &Object_StubDesc,
    DesktopJavaShellExtension__MIDL_ProcFormatString.Format,
    &IIconHandler_FormatStringOffsetTable[-3],
    0,
    0,
    0
    };


static const MIDL_SERVER_INFO IIconHandler_ServerInfo = 
    {
    &Object_StubDesc,
    0,
    DesktopJavaShellExtension__MIDL_ProcFormatString.Format,
    &IIconHandler_FormatStringOffsetTable[-3],
    0,
    0,
    0,
    0};
CINTERFACE_PROXY_VTABLE(7) _IIconHandlerProxyVtbl = 
{
    0,
    &IID_IIconHandler,
    IUnknown_QueryInterface_Proxy,
    IUnknown_AddRef_Proxy,
    IUnknown_Release_Proxy ,
    0 /* (void *) (INT_PTR) -1 /* IDispatch::GetTypeInfoCount */ ,
    0 /* (void *) (INT_PTR) -1 /* IDispatch::GetTypeInfo */ ,
    0 /* (void *) (INT_PTR) -1 /* IDispatch::GetIDsOfNames */ ,
    0 /* IDispatch_Invoke_Proxy */
};


static const PRPC_STUB_FUNCTION IIconHandler_table[] =
{
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION
};

CInterfaceStubVtbl _IIconHandlerStubVtbl =
{
    &IID_IIconHandler,
    &IIconHandler_ServerInfo,
    7,
    &IIconHandler_table[-3],
    CStdStubBuffer_DELEGATING_METHODS
};


/* Object interface: IPropertySheetHandler, ver. 0.0,
   GUID={0x86C3A15B,0xB302,0x4B5C,{0xB6,0x98,0x3D,0x9F,0x71,0x2F,0xA2,0x4E}} */

#pragma code_seg(".orpc")
static const unsigned short IPropertySheetHandler_FormatStringOffsetTable[] =
    {
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    (unsigned short) -1,
    0
    };

static const MIDL_STUBLESS_PROXY_INFO IPropertySheetHandler_ProxyInfo =
    {
    &Object_StubDesc,
    DesktopJavaShellExtension__MIDL_ProcFormatString.Format,
    &IPropertySheetHandler_FormatStringOffsetTable[-3],
    0,
    0,
    0
    };


static const MIDL_SERVER_INFO IPropertySheetHandler_ServerInfo = 
    {
    &Object_StubDesc,
    0,
    DesktopJavaShellExtension__MIDL_ProcFormatString.Format,
    &IPropertySheetHandler_FormatStringOffsetTable[-3],
    0,
    0,
    0,
    0};
CINTERFACE_PROXY_VTABLE(7) _IPropertySheetHandlerProxyVtbl = 
{
    0,
    &IID_IPropertySheetHandler,
    IUnknown_QueryInterface_Proxy,
    IUnknown_AddRef_Proxy,
    IUnknown_Release_Proxy ,
    0 /* (void *) (INT_PTR) -1 /* IDispatch::GetTypeInfoCount */ ,
    0 /* (void *) (INT_PTR) -1 /* IDispatch::GetTypeInfo */ ,
    0 /* (void *) (INT_PTR) -1 /* IDispatch::GetIDsOfNames */ ,
    0 /* IDispatch_Invoke_Proxy */
};


static const PRPC_STUB_FUNCTION IPropertySheetHandler_table[] =
{
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION,
    STUB_FORWARDING_FUNCTION
};

CInterfaceStubVtbl _IPropertySheetHandlerStubVtbl =
{
    &IID_IPropertySheetHandler,
    &IPropertySheetHandler_ServerInfo,
    7,
    &IPropertySheetHandler_table[-3],
    CStdStubBuffer_DELEGATING_METHODS
};

static const MIDL_STUB_DESC Object_StubDesc = 
    {
    0,
    NdrOleAllocate,
    NdrOleFree,
    0,
    0,
    0,
    0,
    0,
    DesktopJavaShellExtension__MIDL_TypeFormatString.Format,
    1, /* -error bounds_check flag */
    0x20000, /* Ndr library version */
    0,
    0x70001f3, /* MIDL Version 7.0.499 */
    0,
    0,
    0,  /* notify & notify_flag routine table */
    0x1, /* MIDL flag */
    0, /* cs routines */
    0,   /* proxy/server info */
    0
    };

const CInterfaceProxyVtbl * _DesktopJavaShellExtension_ProxyVtblList[] = 
{
    ( CInterfaceProxyVtbl *) &_IIconHandlerProxyVtbl,
    ( CInterfaceProxyVtbl *) &_IPropertySheetHandlerProxyVtbl,
    0
};

const CInterfaceStubVtbl * _DesktopJavaShellExtension_StubVtblList[] = 
{
    ( CInterfaceStubVtbl *) &_IIconHandlerStubVtbl,
    ( CInterfaceStubVtbl *) &_IPropertySheetHandlerStubVtbl,
    0
};

PCInterfaceName const _DesktopJavaShellExtension_InterfaceNamesList[] = 
{
    "IIconHandler",
    "IPropertySheetHandler",
    0
};

const IID *  _DesktopJavaShellExtension_BaseIIDList[] = 
{
    &IID_IDispatch,
    &IID_IDispatch,
    0
};


#define _DesktopJavaShellExtension_CHECK_IID(n)	IID_GENERIC_CHECK_IID( _DesktopJavaShellExtension, pIID, n)

int __stdcall _DesktopJavaShellExtension_IID_Lookup( const IID * pIID, int * pIndex )
{
    IID_BS_LOOKUP_SETUP

    IID_BS_LOOKUP_INITIAL_TEST( _DesktopJavaShellExtension, 2, 1 )
    IID_BS_LOOKUP_RETURN_RESULT( _DesktopJavaShellExtension, 2, *pIndex )
    
}

const ExtendedProxyFileInfo DesktopJavaShellExtension_ProxyFileInfo = 
{
    (PCInterfaceProxyVtblList *) & _DesktopJavaShellExtension_ProxyVtblList,
    (PCInterfaceStubVtblList *) & _DesktopJavaShellExtension_StubVtblList,
    (const PCInterfaceName * ) & _DesktopJavaShellExtension_InterfaceNamesList,
    (const IID ** ) & _DesktopJavaShellExtension_BaseIIDList,
    & _DesktopJavaShellExtension_IID_Lookup, 
    2,
    2,
    0, /* table of [async_uuid] interfaces */
    0, /* Filler1 */
    0, /* Filler2 */
    0  /* Filler3 */
};
#pragma optimize("", on )
#if _MSC_VER >= 1200
#pragma warning(pop)
#endif


#endif /* !defined(_M_IA64) && !defined(_M_AMD64)*/

