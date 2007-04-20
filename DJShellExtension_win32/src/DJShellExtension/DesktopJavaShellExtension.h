

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 6.00.0366 */
/* at Fri Apr 20 19:49:56 2007
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


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 440
#endif

#include "rpc.h"
#include "rpcndr.h"

#ifndef __RPCNDR_H_VERSION__
#error this stub requires an updated version of <rpcndr.h>
#endif // __RPCNDR_H_VERSION__

#ifndef COM_NO_WINDOWS_H
#include "windows.h"
#include "ole2.h"
#endif /*COM_NO_WINDOWS_H*/

#ifndef __DesktopJavaShellExtension_h__
#define __DesktopJavaShellExtension_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */ 

#ifndef __IIconHandler_FWD_DEFINED__
#define __IIconHandler_FWD_DEFINED__
typedef interface IIconHandler IIconHandler;
#endif 	/* __IIconHandler_FWD_DEFINED__ */


#ifndef __IPropertySheetHandler_FWD_DEFINED__
#define __IPropertySheetHandler_FWD_DEFINED__
typedef interface IPropertySheetHandler IPropertySheetHandler;
#endif 	/* __IPropertySheetHandler_FWD_DEFINED__ */


#ifndef __IconHandler_FWD_DEFINED__
#define __IconHandler_FWD_DEFINED__

#ifdef __cplusplus
typedef class IconHandler IconHandler;
#else
typedef struct IconHandler IconHandler;
#endif /* __cplusplus */

#endif 	/* __IconHandler_FWD_DEFINED__ */


#ifndef __PropertySheetHandler_FWD_DEFINED__
#define __PropertySheetHandler_FWD_DEFINED__

#ifdef __cplusplus
typedef class PropertySheetHandler PropertySheetHandler;
#else
typedef struct PropertySheetHandler PropertySheetHandler;
#endif /* __cplusplus */

#endif 	/* __PropertySheetHandler_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 

void * __RPC_USER MIDL_user_allocate(size_t);
void __RPC_USER MIDL_user_free( void * ); 

#ifndef __IIconHandler_INTERFACE_DEFINED__
#define __IIconHandler_INTERFACE_DEFINED__

/* interface IIconHandler */
/* [unique][helpstring][nonextensible][dual][uuid][object] */ 


EXTERN_C const IID IID_IIconHandler;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("D3C84433-AA02-408D-8E25-5ACC78D5C530")
    IIconHandler : public IDispatch
    {
    public:
    };
    
#else 	/* C style interface */

    typedef struct IIconHandlerVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IIconHandler * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IIconHandler * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IIconHandler * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IIconHandler * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IIconHandler * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IIconHandler * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IIconHandler * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        END_INTERFACE
    } IIconHandlerVtbl;

    interface IIconHandler
    {
        CONST_VTBL struct IIconHandlerVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IIconHandler_QueryInterface(This,riid,ppvObject)	\
    (This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IIconHandler_AddRef(This)	\
    (This)->lpVtbl -> AddRef(This)

#define IIconHandler_Release(This)	\
    (This)->lpVtbl -> Release(This)


#define IIconHandler_GetTypeInfoCount(This,pctinfo)	\
    (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo)

#define IIconHandler_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo)

#define IIconHandler_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)

#define IIconHandler_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)


#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IIconHandler_INTERFACE_DEFINED__ */


#ifndef __IPropertySheetHandler_INTERFACE_DEFINED__
#define __IPropertySheetHandler_INTERFACE_DEFINED__

/* interface IPropertySheetHandler */
/* [unique][helpstring][nonextensible][dual][uuid][object] */ 


EXTERN_C const IID IID_IPropertySheetHandler;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("86C3A15B-B302-4B5C-B698-3D9F712FA24E")
    IPropertySheetHandler : public IDispatch
    {
    public:
    };
    
#else 	/* C style interface */

    typedef struct IPropertySheetHandlerVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IPropertySheetHandler * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IPropertySheetHandler * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IPropertySheetHandler * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IPropertySheetHandler * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IPropertySheetHandler * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IPropertySheetHandler * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IPropertySheetHandler * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        END_INTERFACE
    } IPropertySheetHandlerVtbl;

    interface IPropertySheetHandler
    {
        CONST_VTBL struct IPropertySheetHandlerVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IPropertySheetHandler_QueryInterface(This,riid,ppvObject)	\
    (This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IPropertySheetHandler_AddRef(This)	\
    (This)->lpVtbl -> AddRef(This)

#define IPropertySheetHandler_Release(This)	\
    (This)->lpVtbl -> Release(This)


#define IPropertySheetHandler_GetTypeInfoCount(This,pctinfo)	\
    (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo)

#define IPropertySheetHandler_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo)

#define IPropertySheetHandler_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)

#define IPropertySheetHandler_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)


#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IPropertySheetHandler_INTERFACE_DEFINED__ */



#ifndef __DesktopJavaShellExtensionLib_LIBRARY_DEFINED__
#define __DesktopJavaShellExtensionLib_LIBRARY_DEFINED__

/* library DesktopJavaShellExtensionLib */
/* [helpstring][version][uuid] */ 


EXTERN_C const IID LIBID_DesktopJavaShellExtensionLib;

EXTERN_C const CLSID CLSID_IconHandler;

#ifdef __cplusplus

class DECLSPEC_UUID("560F06E8-E0EF-4DD6-9733-5C2507132C57")
IconHandler;
#endif

EXTERN_C const CLSID CLSID_PropertySheetHandler;

#ifdef __cplusplus

class DECLSPEC_UUID("5EC050B4-7064-44B9-8D71-EC1A33DFB604")
PropertySheetHandler;
#endif
#endif /* __DesktopJavaShellExtensionLib_LIBRARY_DEFINED__ */

/* Additional Prototypes for ALL interfaces */

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif


