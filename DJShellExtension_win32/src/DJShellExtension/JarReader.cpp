/*
 * Issam Chehab (issam.chehab at gmail dot com)
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
#include "stdafx.h"
#include "JarReader.h"
#include "unzip/unzip.h"
#include "unzip/iowin32.h"
#include "sstream"
#include "bmp/IconCreator.h"

using namespace std;
JarReader::JarReader(LPCWSTR file) : fileName(file), 
                                     jarHandle(0),
                                     hasOwnIcons(false)
{
    if (!openJar()) {
        return;
    }
    loadManifest();
}

JarReader::~JarReader()
{
    closeJar();
}

bool JarReader::openJar()
{
    zlib_filefunc_def ffunc;
    fill_win32_filefunc(&ffunc);
    return (jarHandle = unzOpen2(fileName.c_str(),&ffunc)) != 0;
}
void JarReader::closeJar()
{
    if (jarHandle) {       
        unzClose(jarHandle);
        jarHandle = 0;
    }
}
bool JarReader::readFileFromJar(const char *filename, unsigned int& len, unsigned char * & content)
{
    if (!jarHandle) {
        return false;
    }
    
    if (unzLocateFile(jarHandle,filename,1)!=UNZ_OK) {
        // file not found
        return false;
    }
 
    int offset = 0;
    int err=UNZ_OK;
    unsigned char* buf;
    uInt size_buf;
    unz_file_info file_info;
    uLong ratio=0;

    if (unzGetCurrentFileInfo(jarHandle,&file_info,NULL,0,NULL,0,NULL,0) != UNZ_OK) {
        // error  with zipfile in unzGetCurrentFileInfo
        return false;
    }
    if (unzOpenCurrentFile(jarHandle) != UNZ_OK) {
        // Failed opening requested file!
        return false;
    }
    size_buf = file_info.uncompressed_size;
    buf = (unsigned char*)malloc(size_buf + 1);
    if (buf==NULL) {
        // printf("Error allocating memory\n");
        return false;
    }
    buf[size_buf] = '\0';
    int bytesRead ;
    do {
        if ((bytesRead=unzReadCurrentFile(jarHandle,(char*)buf+offset,size_buf)) < 0) {
            // error  with zipfile in unzReadCurrentFile
            break;
        }
        offset+=bytesRead;
        size_buf-=bytesRead;
    } while (bytesRead>0);

    if (size_buf) 
    {
        free(buf);
        // Uncompression failed, not enough bytes to fill buffer
        return false;
    }
    
    if (unzCloseCurrentFile (jarHandle) != UNZ_OK) {
        // error %d with zipfile in unzCloseCurrentFile\n",err);
        free(buf);
        return false;
    }
    content = buf;
    len = file_info.uncompressed_size;
    return true;
}

/*   
    
   
    const std::wstring MAIN_CLASS_DESC(L"Main-Class-Desc");
    const std::wstring MAIN_CLASS_ARGS(L"Main-Class-Args");
    const std::wstring MAIN_CLASS_VM_ARGS(L"Main-Class-VM-Args");
*/
static const std::wstring MAIN_CLASS(L"Main-Class");
static const std::wstring JAR_ICON_PREFIX(L"Jar-Icon-");
bool JarReader::displayInPropertySheet(const std::wstring& key) const
{
    return true;//key.find(JAR_ICON_PREFIX) != 0;
}
void trim(wstring& str)
{
    if (str.length() == 0) {
        return;
    } 
    if (str[str.length()-1]  == L'\r') {
        str.erase(str.length()-1);
    }
  wstring::size_type pos = str.find_last_not_of(L' ');
  if(pos != wstring::npos) {
    str.erase(pos + 1);
    pos = str.find_first_not_of(' ');
    if(pos != string::npos) str.erase(0, pos);
  }
  else str.erase(str.begin(), str.end());
}
void JarReader::loadManifest()
{
    if (!jarHandle) {
        return;
    }
    unsigned char *content;
    unsigned int len;
    if (!readFileFromJar("META-INF/MANIFEST.MF",len,content)) {
        closeJar();
        return;
    }
    
    std::stringstream manifestStream;
    manifestStream << (char*)(content);
    free(content);

    char line[1000];
    memset(line,0,sizeof(line));
    wstring lastKey;
    while(!manifestStream.eof()) {
        manifestStream.getline(line,1000);        
        string nstr(line);
        wstring str(nstr.begin(),nstr.end());
        trim(str);
        if (str.length() == 0) {
            break;
        }
        if (nstr[0] == ' ') {
            if (manifest.find(lastKey) != manifest.end()) {
                (*manifest.find(lastKey)).second += str.substr(1);
            }
            continue;
        }
        string::size_type idx = str.find_first_of(L':');
        if (idx == string::npos) {
            continue;
        }
        wstring key = str.substr(0,idx);
        wstring val;
        if (idx == str.length()) {
            val = L"";
        } else {
            val = str.substr(idx+1);
        }
        trim(key);
        lastKey = key;
        if (key.find(JAR_ICON_PREFIX,0) == 0 || key.find(MAIN_CLASS,0) == 0) {
            if (key.find(JAR_ICON_PREFIX,0) == 0) {
                hasOwnIcons = true;
            }
        }
        trim(val);
        manifest.insert(pair<wstring,wstring>(key,val));
    }
}


bool JarReader::isRunnable()
{
	if(manifest.find(MAIN_CLASS) != manifest.end())
	{
		wstring str((*manifest.find(MAIN_CLASS)).second);
		trim(str);
		return str.length() > 0;
	}
    return false;
}
bool JarReader::hasDefinedIcons()
{
    return hasOwnIcons;
}
bool JarReader::getIcons(int smallIconWidth, HICON* smallIcon, int largeIconWidth, HICON* largeIcon)
{
    int id;
    if (hasDefinedIcons() && getOwnIcons(smallIconWidth, smallIcon, largeIconWidth, largeIcon)) {
        return true;
    }
    if (isRunnable()) { 
        id = IDI_DJRUNNABLE;
    } else {
        id = IDI_DJLIBRARY;
    }
    
    if (smallIcon) {
        *smallIcon = (HICON)LoadImage(g_hInstance,MAKEINTRESOURCE(id),IMAGE_ICON,smallIconWidth,smallIconWidth,LR_DEFAULTCOLOR);
    } 
    if (largeIcon) {
        *largeIcon = (HICON)LoadImage(g_hInstance,MAKEINTRESOURCE(id),IMAGE_ICON,largeIconWidth,largeIconWidth,LR_DEFAULTCOLOR);
    } 

    return true;
}

bool JarReader::getOwnIcons(int smallIconWidth, HICON* pSmallIcon, int largeIconWidth, HICON* pLargeIcon)
{
    HICON smallIcon = loadIconFromJar(smallIconWidth);
    if (smallIcon != NULL) {
        HICON largeIcon = loadIconFromJar(largeIconWidth);
        *pSmallIcon = smallIcon;
        *pLargeIcon = largeIcon;
        return true;
    }
    return false;
}

static int extractIconX(wstring key) 
{

    int x,y;
    wchar_t c;
    swscanf(key.substr(JAR_ICON_PREFIX.length()).c_str(),L"%d%c%d",&x,&c,&y);
    if (c = L'x' && x == y) {
        return x;
    } else {
        return 0;
    }
}
HICON JarReader::loadIconFromJar(int width)
{
    if (width < 1) {
        return NULL;
    }
    map<int,wstring*> resmap;
    
    for(map<wstring,wstring>::iterator it = manifest.begin();it != manifest.end();it++) {
        if ((*it).first.find(JAR_ICON_PREFIX,0)==0) {
            int x = extractIconX((*it).first);
            if (x > 0) {
                resmap.insert(pair<int,wstring*>(x,&(*it).second));
            }
        }
    }

    HICON hIcon = NULL;
    map<int,wstring*>::iterator closestMatch = resmap.end();
    for(map<int,wstring*>::iterator it = resmap.begin();it != resmap.end();it++) {
        if ((*it).first < width) {
            continue;
        }
        closestMatch = it;
        break;
    }


    if (closestMatch != resmap.end()) {
        for(map<int,wstring*>::iterator it = closestMatch;it != resmap.end();it++) {
            hIcon = convertJarIconToIco(*(*it).second,width);
            if (hIcon != 0) {
                return hIcon;
            }
        }
        if (closestMatch != resmap.end()) {
            closestMatch--;
        }


        for(map<int,wstring*>::iterator it = closestMatch;it != resmap.end();it--) {
            hIcon = convertJarIconToIco(*(*it).second,width);
            if (hIcon != 0) {
                return hIcon;
            }
        }
    } else {
        for(map<int,wstring*>::reverse_iterator it = resmap.rbegin();it != resmap.rend();it++) {
            hIcon = convertJarIconToIco(*(*it).second,width);
            if (hIcon != 0) {
                return hIcon;
            }
        }
    }
    return NULL;
}


HICON JarReader::convertJarIconToIco(wstring& fname,unsigned int width)
{
    wstring filename = fname;
    if (filename[0] == '/') {
        filename.erase(0,1);
    }
    //
    string narrowFilename(filename.begin(),filename.end());
    unsigned int len = 0;
    unsigned char *content;
    if (!readFileFromJar(narrowFilename.c_str(),len,content)) {
        return NULL;
    }
    HICON hIcon =  IconCreator::convertToBmp(content,len,width);
    free(content);
    return hIcon;
}

