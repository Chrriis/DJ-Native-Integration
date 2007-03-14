/*
 * Issam Chehab (issam.chehab at gmail dot com)
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
// IconHandler.h : Declaration of the CIconHandler

#pragma once
#include "resource.h"       // main symbols
#include "shlobj.h"
#include "DesktopJavaShellExtension.h"
#include "JarReader.h"

#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Single-threaded COM objects are not properly supported on Windows CE platform, such as the Windows Mobile platforms that do not include full DCOM support. Define _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA to force ATL to support creating single-thread COM object's and allow use of it's single-threaded COM object implementations. The threading model in your rgs file was set to 'Free' as that is the only threading model supported in non DCOM Windows CE platforms."
#endif



// CIconHandler

class ATL_NO_VTABLE CIconHandler :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CIconHandler, &CLSID_IconHandler>,
	public IDispatchImpl<IIconHandler, &IID_IIconHandler, &LIBID_DesktopJavaShellExtensionLib, /*wMajor =*/ 1, /*wMinor =*/ 0>,
    public IPersistFile, public IExtractIcon
{
public:
    CIconHandler() 
	{
	}

DECLARE_REGISTRY_RESOURCEID(IDR_ICONHANDLER)

DECLARE_NOT_AGGREGATABLE(CIconHandler)

BEGIN_COM_MAP(CIconHandler)
	COM_INTERFACE_ENTRY(IIconHandler)
	COM_INTERFACE_ENTRY(IDispatch)
    COM_INTERFACE_ENTRY(IPersistFile)
    COM_INTERFACE_ENTRY(IExtractIcon)
END_COM_MAP()



	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
        jarReader = 0;
		return S_OK;
	}

	void FinalRelease()
	{
        deleteJarReader();
	}
private:
    JarReader* jarReader;
    void deleteJarReader()
    {
        if (jarReader) {
            delete jarReader;
            jarReader = 0;
        }
    }

public:

  // IPersistFile
  STDMETHOD(GetClassID)( CLSID* );
  STDMETHOD(IsDirty)();
  STDMETHOD(Save)( LPCOLESTR, BOOL );
  STDMETHOD(SaveCompleted)( LPCOLESTR );
  STDMETHOD(GetCurFile)( LPOLESTR* );
  STDMETHOD(Load)( LPCOLESTR wszFile, DWORD /*dwMode*/ );

  // IExtractIcon
  STDMETHODIMP GetIconLocation(UINT uFlags, LPTSTR szIconFile, UINT cchMax,
                               int* piIndex, UINT* pwFlags);
  STDMETHODIMP Extract(LPCTSTR pszFile, UINT nIconIndex, HICON* phiconLarge,
                       HICON* phiconSmall, UINT nIconSize);
};

OBJECT_ENTRY_AUTO(__uuidof(IconHandler), CIconHandler)
