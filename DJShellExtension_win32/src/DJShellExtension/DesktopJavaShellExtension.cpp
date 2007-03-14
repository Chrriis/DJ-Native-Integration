/*
 * Issam Chehab (issam.chehab at gmail dot com)
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
// DesktopJavaShellExtension.cpp : Implementation of DLL Exports.


#include "stdafx.h"
#include "resource.h"
#include "DesktopJavaShellExtension.h"
#include <atltrace.h>


class CDesktopJavaShellExtensionModule : public CAtlDllModuleT< CDesktopJavaShellExtensionModule >
{
public :
	DECLARE_LIBID(LIBID_DesktopJavaShellExtensionLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_DESKTOPJAVASHELLEXTENSION, "{026661D7-B1F2-46B4-8A67-17D07B06AB1A}")
};

CDesktopJavaShellExtensionModule _AtlModule;
HINSTANCE g_hInstance;
FILE *logFile;
#ifdef _MANAGED
#pragma managed(push, off)
#endif

#include "JarReader.h"
int i;
// DLL Entry Point

extern "C" BOOL WINAPI DllMain(HINSTANCE hInstance, DWORD dwReason, LPVOID lpReserved)
{
	g_hInstance = hInstance;
    ATLTRACE2( atlTraceCOM, 0,"DLL MAIN Loading %d\n",++i);
    BOOL val = _AtlModule.DllMain(dwReason, lpReserved);  

    return val;
}

#ifdef _MANAGED
#pragma managed(pop)
#endif



// Used to determine whether the DLL can be unloaded by OLE
STDAPI DllCanUnloadNow(void)
{
    ATLTRACE2( atlTraceCOM, 0,"DLL MAIN UnLoading %d\n",++i);
    return _AtlModule.DllCanUnloadNow();
}


// Returns a class factory to create an object of the requested type
STDAPI DllGetClassObject(REFCLSID rclsid, REFIID riid, LPVOID* ppv)
{
    ATLTRACE2( atlTraceCOM, 0,"DLL GetClassObject %d\n",++i);
    return _AtlModule.DllGetClassObject(rclsid, riid, ppv);
}


// DllRegisterServer - Adds entries to the system registry
STDAPI DllRegisterServer(void)
{
    // registers object, typelib and all interfaces in typelib
    HRESULT hr = _AtlModule.DllRegisterServer();
	return hr;
}


// DllUnregisterServer - Removes entries from the system registry
STDAPI DllUnregisterServer(void)
{
	HRESULT hr = _AtlModule.DllUnregisterServer();
	return hr;
}

