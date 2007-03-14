/*
 * Issam Chehab (issam.chehab at gmail dot com)
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
// PropertySheetHandler.h : Declaration of the CPropertySheetHandler
#pragma once
#include "resource.h"       // main symbols
#include "shlobj.h"
#include "list"
#include "DesktopJavaShellExtension.h"
#include "JarReader.h"

#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Single-threaded COM objects are not properly supported on Windows CE platform, such as the Windows Mobile platforms that do not include full DCOM support. Define _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA to force ATL to support creating single-thread COM object's and allow use of it's single-threaded COM object implementations. The threading model in your rgs file was set to 'Free' as that is the only threading model supported in non DCOM Windows CE platforms."
#endif



// CPropertySheetHandler

class ATL_NO_VTABLE CPropertySheetHandler :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CPropertySheetHandler, &CLSID_PropertySheetHandler>,
    public IShellExtInit, public IShellPropSheetExt
{
public:
	CPropertySheetHandler()
	{
	}

DECLARE_REGISTRY_RESOURCEID(IDR_PROPERTYSHEETHANDLER)


BEGIN_COM_MAP(CPropertySheetHandler)
    COM_INTERFACE_ENTRY(IShellExtInit)
    COM_INTERFACE_ENTRY(IShellPropSheetExt)
END_COM_MAP()



	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
		return S_OK;
	}

	void FinalRelease()
	{
	}

protected:
    std::wstring fileName;
public:

    STDMETHODIMP Initialize (LPCITEMIDLIST pidlFolder, LPDATAOBJECT pDataObj, HKEY hProgID );
    STDMETHODIMP AddPages(LPFNADDPROPSHEETPAGE, LPARAM);
    STDMETHODIMP ReplacePage(UINT, LPFNADDPROPSHEETPAGE, LPARAM)
      { return E_NOTIMPL; }
};

OBJECT_ENTRY_AUTO(__uuidof(PropertySheetHandler), CPropertySheetHandler)
