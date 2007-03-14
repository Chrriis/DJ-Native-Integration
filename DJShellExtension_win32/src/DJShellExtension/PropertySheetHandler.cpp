/*
 * Issam Chehab (issam.chehab at gmail dot com)
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
// PropertySheetHandler.cpp : Implementation of CPropertySheetHandler

#include "stdafx.h"
#include "PropertySheetHandler.h"


// CPropertySheetHandler

STDMETHODIMP CPropertySheetHandler::Initialize (LPCITEMIDLIST pidlFolder, LPDATAOBJECT pDataObj, HKEY hProgID )
{ 
    TCHAR     szFile[MAX_PATH];
    UINT      uNumFiles;
    HDROP     hdrop;
    FORMATETC etc = { CF_HDROP, NULL, DVASPECT_CONTENT, -1, TYMED_HGLOBAL };
    STGMEDIUM stg;
    INITCOMMONCONTROLSEX iccex = { sizeof(INITCOMMONCONTROLSEX), ICC_DATE_CLASSES };
    // Init the common controls.
    InitCommonControlsEx ( &iccex );
    // Read the list of items from the data object.  They're stored in HDROP
    // form, so just get the HDROP handle and then use the drag 'n' drop APIs
    // on it.
    if ( FAILED( pDataObj->GetData ( &etc, &stg ) ))
        return E_INVALIDARG;

    // Get an HDROP handle.
    hdrop = (HDROP) GlobalLock ( stg.hGlobal );

    if ( NULL == hdrop )
    {
        ReleaseStgMedium ( &stg );
        return E_INVALIDARG;
    }

    // Determine how many files are involved in this operation.
    uNumFiles = DragQueryFile ( hdrop, 0xFFFFFFFF, NULL, 0 );
    if (uNumFiles != 1 ||
          0 == DragQueryFile ( hdrop, 0, szFile, MAX_PATH ) || 
          PathIsDirectory ( szFile )) {
        GlobalUnlock ( stg.hGlobal );
        ReleaseStgMedium ( &stg );
        return E_INVALIDARG;
    }
    // Add the filename to our list of files to act on.
    fileName = szFile;
    GlobalUnlock ( stg.hGlobal );
    ReleaseStgMedium ( &stg );
    return (fileName.length() > 0) ? S_OK : E_FAIL;
}

using namespace std;
BOOL CALLBACK PropPageDlgProc ( HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam );
UINT CALLBACK PropPageCallbackProc ( HWND hwnd, UINT uMsg, LPPROPSHEETPAGE ppsp );
BOOL OnInitDialog ( HWND hwnd, LPARAM lParam );
BOOL OnApply ( HWND hwnd, PSHNOTIFY* phdr );
extern HINSTANCE g_hInstance;
STDMETHODIMP CPropertySheetHandler::AddPages(LPFNADDPROPSHEETPAGE lpfnAddPageProc, LPARAM lParam)
{
    PROPSHEETPAGE  psp;
    HPROPSHEETPAGE hPage;
    CAtlBaseModule _Module;

    LPCTSTR szFile = _tcsdup ( fileName.c_str() );
    psp.dwSize      = sizeof(PROPSHEETPAGE);
    psp.dwFlags     = PSP_USETITLE | PSP_USECALLBACK;//PSP_USEREFPARENT |  ;
    psp.hInstance   = _Module.GetResourceInstance(); 
    psp.pszTemplate = MAKEINTRESOURCE(IDD_PROPPAGE_SMALL);
    psp.pszTitle    = _T("Manifest");
    psp.pfnDlgProc  = PropPageDlgProc;
    psp.lParam      = (LPARAM) szFile;
    psp.pfnCallback = PropPageCallbackProc;
    hPage = CreatePropertySheetPage ( &psp );
    if ( NULL != hPage )
    {
        // Call the shell's callback function, so it adds the page to
        // the property sheet.
        if ( !lpfnAddPageProc ( hPage, lParam ) )
            DestroyPropertySheetPage ( hPage );
    }

    return S_OK;
}


BOOL CALLBACK PropPageDlgProc ( HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam )
{
    if (uMsg != WM_INITDIALOG) {
        return FALSE;
    }

    LV_COLUMN col;
    memset(&col,0,sizeof(col));
    col.mask = LVCF_TEXT|LVCF_WIDTH;
    col.cx = 100;
    col.pszText = L"Key";
    SendDlgItemMessage(hwnd,IDC_MANIFEST,LVM_INSERTCOLUMN ,0,(LPARAM)&col);
    col.cx = 200;
    col.pszText = L"Value";
    SendDlgItemMessage(hwnd,IDC_MANIFEST,LVM_INSERTCOLUMN ,1,(LPARAM)&col);
    JarReader jr((LPCWSTR)((LPPROPSHEETPAGE)lParam)->lParam);
    SendDlgItemMessage(hwnd,IDC_MANIFEST,LVM_SETEXTENDEDLISTVIEWSTYLE ,LVS_EX_FULLROWSELECT,LVS_EX_FULLROWSELECT);

    int i = 0;
    const JarReader::manifest_map manifest = jr.getManifest();
    for(JarReader::manifest_map::const_iterator it = manifest.begin();
        it != manifest.end();it++) {
            if (!jr.displayInPropertySheet((*it).first)) {
                continue;
            }
            LVITEM item;
            item.mask = LVIF_TEXT;
            item.iItem = 0;
            item.iSubItem = 0;
            item.pszText = (LPWSTR)(*it).first.c_str();
            i = SendDlgItemMessage(hwnd,IDC_MANIFEST,LVM_INSERTITEM,0,(LPARAM)&item);
            item.mask = LVIF_TEXT;
            item.iItem = i ;
            item.iSubItem = 1;
            item.pszText = (LPWSTR)_tcsdup ( (*it).second.c_str());
            SendDlgItemMessage(hwnd,IDC_MANIFEST,LVM_SETITEM,0,(LPARAM)&item);

    }
    return TRUE;
}

UINT CALLBACK PropPageCallbackProc ( HWND hwnd, UINT uMsg, LPPROPSHEETPAGE ppsp )
{
  if ( PSPCB_RELEASE == uMsg )
    free ( (void*) ppsp->lParam );
 
  return 1;
}
