/*
 * Issam Chehab (issam.chehab at gmail dot com)
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
#include <string>
#include <map>
class JarReader {
public:
    typedef std::map<std::wstring,std::wstring> manifest_map;
private:
    std::wstring fileName;
    manifest_map manifest;
    void* jarHandle;
    bool hasOwnIcons;

protected:
    bool openJar();
    void closeJar();
    bool readFileFromJar(const char *filename, unsigned int& len, unsigned char * & content);

    void loadManifest();
    bool isRunnable();
    bool hasDefinedIcons();

    bool getOwnIcons(int smallIconWidth, HICON* pSmallIcon, int largeIconWidth, HICON* pLargeIcon);
    HICON loadIconFromJar(int width);
    HICON convertJarIconToIco(std::wstring& fname,unsigned int width);
public:
  
    const manifest_map getManifest() const 
    {
        return manifest;
    }
    bool getIcons(int smallIconWidth, HICON* smallIcon, int largeIconWidth, HICON* largeIcon);
    bool displayInPropertySheet(const std::wstring& key) const;
    JarReader(LPCWSTR file);
    virtual ~JarReader();
};