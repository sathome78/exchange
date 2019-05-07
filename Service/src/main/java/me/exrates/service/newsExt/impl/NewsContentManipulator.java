package me.exrates.service.newsExt.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.model.dto.newsDto.NewsDto;
import me.exrates.model.dto.newsDto.NewsListDto;
import me.exrates.model.dto.newsDto.NewsSyncDataDto;
import me.exrates.model.dto.newsDto.NewsTopicDto;
import me.exrates.model.enums.NewsTypeEnum;
import me.exrates.service.exception.NewsContentNotSetException;
import me.exrates.service.exception.NewsTitleImageNotSetException;
import me.exrates.service.exception.UnrecognisedUrlPathForNewsTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * SYNC version for project: exrates <-> edinarCoin
 */

@Component
@Log4j
public class NewsContentManipulator {
  @Value("${news.ext.locationDir}")
  private String newsLocationDir;
  @Value("${news.tempImageFolderUnderLocationDir}")
  private String tempImageUploadFolder;
  @Value("${news.tempFileFolderUnderLocationDir}")
  private String tempFileUploadFolder;
  @Value("${news.newstopic.urlPath}")
  private String newsUrlPath;
  @Value("${news.materialsView.urlPath}")
  private String materialsViewUrlPath;
  @Value("${news.webinar.urlPath}")
  private String webinarUrlPath;
  @Value("${news.event.urlPath}")
  private String eventUrlPath;
  @Value("${news.feastDay.urlPath}")
  private String feastDayUrlPath;
  @Value("${news.page.urlPath}")
  private String pageUrlPath;
  @Value("${news.flattenResourceLocationDir}")
  private Boolean flattenResourceLocationDir;
  @Value("${news.titleImgDir}")
  private String titleImgDir;
  @Value("${news.titleImgFileName}")
  private String titleImgFileName;
  @Value("${news.titleSourceOnDisk}")
  private Boolean titleSourceOnDisk;
  @Value("${news.briefSourceOnDisk}")
  private Boolean briefSourceOnDisk;
  @Value("${news.contentSourceOnDisk}")
  private Boolean contentSourceOnDisk;

  private Map<String, String> replaceConformityMap;

  @PostConstruct
  private void init() {
    replaceConformityMap = new HashMap<String, String>() {{
      put("img/", tempImageUploadFolder);
      put("file/", tempFileUploadFolder);
    }};
  }

  private String getUrlPath(NewsTypeEnum newsTypeEnum) {
    switch (newsTypeEnum) {
      case NEWS:
        return newsUrlPath;
      case MATERIALS:
        return materialsViewUrlPath;
      case WEBINAR:
        return webinarUrlPath;
      case EVENT:
        return eventUrlPath;
      case FEASTDAY:
        return feastDayUrlPath;
      case PAGE:
        return pageUrlPath;
    }
    throw new UnrecognisedUrlPathForNewsTypeException(newsTypeEnum.name());
  }

  private List<String> getAllUrlPaths() {
    return new ArrayList<String>() {{
      add(newsUrlPath);
      add(materialsViewUrlPath);
      add(webinarUrlPath);
      add(eventUrlPath);
      add(feastDayUrlPath);
      add(pageUrlPath);
    }};
  }

  public String getResourcePathToUploadedFile(String uploadedFileName, NewsTypeEnum newsTypeEnum) {
    String urlPath = getUrlPath(newsTypeEnum);
    return urlPath
        .concat("/")
        .concat(uploadedFileName); //    newstopic/2016/AUGUST/10/img/xxx.jpg
  }

  public NewsDto correctResourcesPath(NewsDto newsDto) {
    String resource = newsDto.getResource();
    newsDto.setResource(getCorrectResourcesPath(resource));
    return newsDto;
  }

  public String getCorrectResourcesPath(String resource) {
    if (resource == null) {
      return null;
    }
    if (flattenResourceLocationDir) {
      return resource.replaceAll("/", "_");
    }
    return resource;
  }

  public NewsDto setReferenceToNewstopicPage(NewsDto newsDto) {
    String urlPath = getUrlPath(newsDto.getNewsType());
    newsDto.setReferenceToNewstopic(urlPath
        .concat("/")
        .concat(newsDto.getResource() == null ? "" : newsDto.getResource())
        .concat(newsDto.getId().toString())
        .concat("/")
        .concat("page")
        .concat("/")
        .concat(newsDto.getLanguage()));
    return newsDto;
  }

  public NewsDto setTitleImageSource(NewsDto newsDto) throws IOException {
    if (newsDto.getNoTitleImg()) {
      newsDto.setTitleImageSource(null);
      return newsDto;
    }
    newsDto.setTitleImageSource(
        getTitleFileResourceSource(
            newsDto
        ));
    return newsDto;
  }

  public NewsDto setTitleAndBrief(NewsDto newsDto) {
    if (titleSourceOnDisk) {
      try {
        newsDto.setTitle(getTitleFromDisk(newsDto));
      } catch (IOException e) {
        log.error("not found title on disk for news id: ".concat(newsDto.getId().toString()));
      }
    }
    if (briefSourceOnDisk) {
      try {
        newsDto.setBrief(getBriefFromDisk(newsDto));
      } catch (IOException e) {
        log.error("not found brief on disk for news id: ".concat(newsDto.getId().toString()));
      }
    }
    return newsDto;
  }

  private String getTitleFromDisk(NewsDto news) throws IOException {
    String absulutePathToContent = getAbsolutePathToResourceContent(
        news.getResource(),
        news.getId(),
        news.getLanguage(),
        "title.md"
    );
    try {
      String result = new String(Files.readAllBytes(Paths.get(absulutePathToContent)), "UTF-8");
      log.debug("\n\tread: ".concat(absulutePathToContent));
      return result;
    } catch (Exception e) {
      log.error("\n\t try read with error: ".concat(absulutePathToContent));
      throw e;
    }
  }

  private String getBriefFromDisk(NewsDto news) throws IOException {
    String absulutePathToContent = getAbsolutePathToResourceContent(
        news.getResource(),
        news.getId(),
        news.getLanguage(),
        "brief.md"
    );
    try {
      String result = new String(Files.readAllBytes(Paths.get(absulutePathToContent)), "UTF-8");
      log.debug("\n\tread: ".concat(absulutePathToContent));
      return result;
    } catch (Exception e) {
      log.error("\n\t try read with error: ".concat(absulutePathToContent));
      throw e;
    }
  }

  public NewsDto setContent(NewsTopicDto newsTopicDto) {
    if (contentSourceOnDisk) {
      try {
        newsTopicDto.setContent(getContentFromDisk(newsTopicDto));
      } catch (IOException e) {
        log.error("not found content for news id: ".concat(newsTopicDto.getId().toString()));
      }
    }
    return newsTopicDto;
  }

  private String getContentFromDisk(NewsTopicDto newsTopicDto) throws IOException {
    String absulutePathToContent = getAbsolutePathToResourceContent(
        newsTopicDto.getResource(),
        newsTopicDto.getId(),
        newsTopicDto.getLanguage(),
        "newstopic.html"
    );
    try {
      String content = new String(Files.readAllBytes(Paths.get(absulutePathToContent)), "UTF-8");
      log.debug("\n\tread: ".concat(absulutePathToContent));
      return content;
    } catch (Exception e) {
      log.error("\n\t try read with error: ".concat(absulutePathToContent));
      throw e;
    }
  }

  public String getAbsolutePathToResourceContent(String relativePath, Integer resourceId, String resourceLanguage, String resourceFileName) {
    return newsLocationDir          //    <news.locationDir>
        .concat(relativePath) //                            2015/MAY/27/
        .concat(resourceId.toString()) //                                  48
        .concat("/")                //                                          /
        .concat(resourceLanguage)   //                                              ru
        .concat("/")  //                                                                /
        .concat(resourceFileName);  //                                                       newstopic.html
  }

  public NewsTopicDto replaceReferencesInHtmlToAbsoluteResourcesPath(NewsTopicDto newsTopicDto) {
    String newHtml = replaceReferencesInHtmlToAbsoluteResourcesPath(
        newsTopicDto.getContent(),
        newsTopicDto.getId(),
        newsTopicDto.getNewsType(),
        newsTopicDto.getResource(),
        newsTopicDto.getLanguage()
    );
    newsTopicDto.setContent(newHtml);
    return newsTopicDto;    //  src='..img/picture.png' -> src='/newstopic/2015/MAY/27/48/img/picture.png'
  }

  private String replaceReferencesInHtmlToAbsoluteResourcesPath(
      String html,
      Integer newsId,
      NewsTypeEnum newsTypeEnum,
      String resources,
      String language) {
    if (html == null || resources == null) {
      return html;
    }
    String urlPath = getUrlPath(newsTypeEnum);
    resources = getCorrectResourcesPath(resources);
    String basePath = urlPath
        .concat("/")
        .concat(resources)
        .concat(String.valueOf(newsId))
        .concat("/")
        .concat(language); //   /newstopic/2015/MAY/27/48/ru/
    List<String> maskForProtectedReferenceList = getAllUrlPaths();
    Map<String, String> replacementMap = getRefToResourcesPathReplacementMap(getReferenses(html), basePath, maskForProtectedReferenceList);
    for (Map.Entry<String, String> pair : replacementMap.entrySet()) {
      html = html.replaceAll("=\\s*[']\\s*{1}" + pair.getKey(), "='".concat(pair.getValue()));
      html = html.replaceAll("=\\s*[\"]\\s*{1}" + pair.getKey(), "=\"".concat(pair.getValue()));
    }
    return html; //  src='..img/picture.png' -> src='/newstopic/2015/MAY/27/48/img/picture.png'
  }

  public String replaceAbsoluteResourcesPathInHtmlToReferences(String html) {
    if (StringUtils.isEmpty(html)) {
      return html;
    }
    Map<String, String> replacementMap = getResourcesToRefPathReplacementMap(
        getReferenses(html));
    for (Map.Entry<String, String> pair : replacementMap.entrySet()) {
      html = html.replaceAll("=\\s*[']\\s*{1}" + pair.getKey(), "='".concat(pair.getValue()));
      html = html.replaceAll("=\\s*[\"]\\s*{1}" + pair.getKey(), "=\"".concat(pair.getValue()));
    }
    return html;
  }

  public String convertFileLinkToDownloadLink(String html) {
    if (StringUtils.isEmpty(html)) {
      return html;
    }
    Matcher matcher = Pattern.compile("(<a\\s+href\\s*=\\s*[\"']([\\d\\D&&[^\"']]*)[\"']+[\\d\\D&&[^>]]*>)").matcher(html);
    while (matcher.find()) {
      String aTag = matcher.group(1);
      String href = matcher.group(2);
      if (!href.matches("^[\"']*http.*") && !aTag.contains("download")) {
        String newATag = aTag.replaceAll(">", " download target='_self'>");
        html = html.replaceAll(aTag, newATag);
      }
    }
    return html;
  }

  private Map<String, String> getResourcesToRefPathReplacementMap(
      List<String> refList) {
    Map<String, String> result = new HashMap<>();
    for (Map.Entry<String, String> pair : replaceConformityMap.entrySet()) {
      String newReferencePathForNewInsertedReference = "../".concat(pair.getKey()); //   "../img/"
      String maskForReplacingNewInsertedReference = "/".concat(pair.getValue()); //      "/temp_img_upload/"
      for (String ref : refList) {
        if (ref.contains(maskForReplacingNewInsertedReference)) {
          String fileName = ref.split("/")[ref.split("/").length - 1];
          result.put(ref, newReferencePathForNewInsertedReference.concat(fileName));
        }
      }
    }
    return result;
  }

  public String createAndGetNewsLanguageFolder(String lang, String newsRoot) throws IOException {
    String newsLang = newsRoot
        .concat(lang)
        .concat("/");
    Path langPath = Paths.get(newsLang);
    if (!langPath.toFile().exists()) {
      Files.createDirectories(langPath);
    }
    return newsLang;
  }

  public String createAndGetNewsRootFolder(String resourcePath, Integer newsId) throws IOException {
    String newsRoot = getNewsRootFolder(resourcePath, newsId);
    Path rootPath = Paths.get(newsRoot);
    if (!rootPath.toFile().exists()) {
      Files.createDirectories(rootPath);
    }
    return newsRoot;
  }

  public String getNewsRootFolder(String resourcePath, Integer newsId) throws IOException {
    resourcePath = resourcePath
        .concat(newsId.toString());
    if (flattenResourceLocationDir) {
      resourcePath = resourcePath.replaceAll("/", "_");
    }
    String newsRoot = newsLocationDir
        .concat(resourcePath)
        .concat("/");
    return newsRoot;
  }

  public void moveNewInsertedFileFromTempFolderToNewsFolder(String originalContent, String newsRoot) throws IOException {
    for (Map.Entry<String, String> pair : replaceConformityMap.entrySet()) {
      String newsResourceFolder = newsRoot.concat(pair.getKey()); //   c:/DEVELOPING/NEWS/2016_DECEMBER_21_230/img/
      String tempResourceFolder = pair.getValue(); //  "temp_img_upload/"
      Path resourcePath = Paths.get(newsResourceFolder);
      if (!resourcePath.toFile().exists()) {
        Files.createDirectories(resourcePath);
      }
      List<String> insertedFiles = getNewsInsertedFiles(
          originalContent,
          "/".concat(tempResourceFolder));
      String fullPathToTempImage = newsLocationDir
          .concat(tempResourceFolder); // C:/DEVELOPING/NEWS/temp_img_upload
      for (String resourceFile : insertedFiles) {
        String newsResourceFile = newsResourceFolder
            .concat(resourceFile);
        Path resourceFilePath = Paths.get(newsResourceFile);
        if (resourceFilePath.toFile().exists()) {
          resourceFilePath.toFile().delete();
        }
        Path tempFilePath = Paths.get(fullPathToTempImage.concat(resourceFile));
        Files.move(tempFilePath, resourceFilePath);
      }
    }
  }

  public void storeNewsTitleIntoFile(String title, String newsLang) throws IOException {
    if (title == null) {
      title = "";
//      throw new NewsTitleNotSetException("Title must be set");
    }
    String newsTitle = newsLang
        .concat("/title.md");
    Path titlePath = Paths.get(newsTitle);
    if (titlePath.toFile().exists()) {
      titlePath.toFile().delete();
    }
    Files.write(titlePath, title.getBytes("UTF-8"));
  }

  public void storeNewsBriefIntoFile(String brief, String newsLang) throws IOException {
    if (brief == null) {
      brief = "";
    }
    String newsBrief = newsLang
        .concat("/brief.md");
    Path briefPath = Paths.get(newsBrief);
    if (briefPath.toFile().exists()) {
      briefPath.toFile().delete();
    }
    Files.write(briefPath, brief.getBytes("UTF-8"));
  }

  public void storeNewsContentIntoFile(String convertedContent, String newsLang) throws IOException {
    if (convertedContent == null) {
      throw new NewsContentNotSetException("Content must be set");
    }
    String newstopic = convertedContent;
    String newsNewstopic = newsLang
        .concat("/newstopic.html");
    Path newstopicPath = Paths.get(newsNewstopic);
    if (newstopicPath.toFile().exists()) {
      newstopicPath.toFile().delete();
    }
    Files.write(newstopicPath, newstopic.getBytes("UTF-8"));
  }

  public void storeNewsTitleImageIntoFile(Integer newsId, String htmlWithTitleImg, String newsRoot, String newsType) throws IOException {
    NewsTypeEnum newsTypeEnum = NewsTypeEnum.convert((newsType));
    List<String> insertedTitleImgFiles = getNewsInsertedFiles(
        htmlWithTitleImg,
        "/".concat(tempImageUploadFolder));
    if (insertedTitleImgFiles.size() == 0 && !htmlWithTitleImg.matches("^.*<img\\s+src\\s*=.*$")) {
      throw new NewsTitleImageNotSetException("Title image must be set !");
    }
    if (insertedTitleImgFiles.size() == 0) {
      return;
    }
    TitleImgParams titleImgParams = getTitleImgParams(newsId, newsRoot);
    deleteExistingTitleFileNames(titleImgParams);
            /**/
    String fullPathToTitleImage = newsLocationDir.concat(tempImageUploadFolder);
    String titleImgFile = insertedTitleImgFiles.get(0);
    String ext = titleImgFile.split("\\.")[1];
    String newsTitleImgFile = titleImgParams.newsTitleImgFolder
        .concat(titleImgParams.newsTitleImgFileName)
        .concat(".")
        .concat(ext);
    Path titleImgFilePath = Paths.get(newsTitleImgFile);
            /**/
    Path tempFilePath = Paths.get(fullPathToTitleImage.concat(titleImgFile));
    Files.move(tempFilePath, titleImgFilePath);
  }

  public TitleImgParams getTitleImgParams(Integer newsId, String newsRoot) {
    TitleImgParams result = new TitleImgParams();
    if (StringUtils.isEmpty(titleImgDir)) {
      result.newsTitleImgFolder = newsRoot;
      result.newsTitleImgFileName = titleImgFileName;
    } else {
      result.newsTitleImgFolder = newsLocationDir.concat(titleImgDir);
      result.newsTitleImgFileName = newsId.toString();
    }
    return result;
  }

  /*https://www.youtube.com/embed/kiyns5i80Uw?list=PLV7gk9EH4MgXRDQHHfAnHWkct3g-cr_Eg*/
  public void setVideoId(NewsListDto newsListDto) {
    String fullUrl = newsListDto.getResource();
    String id = "";
    if (fullUrl.contains("embed")) {
      id = fullUrl.split("\\/")[4].split("\\?")[0];
    }
    newsListDto.setYtVideoId(id);
  }

  public static class TitleImgParams {
    private String newsTitleImgFolder;
    private String newsTitleImgFileName;
  }

  public Map<String, String> getAbsoluteFileNamesOfResources(NewsSyncDataDto newsSyncDataDto) {
    Map<String, String> absoluteFileNamesMapForNews = new HashMap<>();
    for (NewsSyncDataDto.NewsVariantSyncData newsVariantSyncData : newsSyncDataDto.getVariantList()) {
      if (newsVariantSyncData.getActive()) {
        String contentWithAbsolutePathes = replaceReferencesInHtmlToAbsoluteResourcesPath(
            newsVariantSyncData.getContent(),
            newsSyncDataDto.getId(),
            newsSyncDataDto.getNewsType(),
            newsSyncDataDto.getResources(),
            newsVariantSyncData.getLanguage()
        );
        newsVariantSyncData.setContent(contentWithAbsolutePathes);
        absoluteFileNamesMapForNews.putAll(
            getImgAbsoluteFilePath(contentWithAbsolutePathes)
        );
        absoluteFileNamesMapForNews.putAll(
            getDownloadableFileAbsoluteFilePath(contentWithAbsolutePathes)
        );
      }
    }
    return absoluteFileNamesMapForNews;
  }

  public Map<String, String> getImgAbsoluteFilePath(String html) {
    Map<String, String> result = new HashMap<>();
    if (StringUtils.isEmpty(html)) {
      return result;
    }
    List<String> allImgReferences = getImgReferenses(html);
    for (int i = 0; i < allImgReferences.size(); i++) {
      String reference = allImgReferences.get(i);
      String absoluteDiskPath = getAbsoluteFilePathFromReference(reference);
      String referenceLabel = reference.replaceAll("/", ":");
      result.put(referenceLabel, absoluteDiskPath);
    }
    return result;
  }

  public Map<String, String> getDownloadableFileAbsoluteFilePath(String html) {
    Map<String, String> result = new HashMap<>();
    if (StringUtils.isEmpty(html)) {
      return result;
    }
    List<String> allImgReferences = getDownloadableFileReferenses(html);
    for (int i = 0; i < allImgReferences.size(); i++) {
      String reference = allImgReferences.get(i);
      String absoluteDiskPath = getAbsoluteFilePathFromReference(reference);
      String referenceLabel = reference.replaceAll("/", ":");
      result.put(referenceLabel, absoluteDiskPath);
    }
    return result;
  }

  public String getAbsoluteFilePathFromReference(String reference) {
    for (String urlPath : getAllUrlPaths()) {
      reference = reference
          .replaceAll("^".concat(urlPath), newsLocationDir)
          .replaceAll("//", "/");
    }
    return reference;
  }

  private List<String> getNewsInsertedFiles(String html, String maskForNewInsertedReference) {
    List<String> allReferences = getReferenses(html);
    return allReferences.stream()
        .filter(e -> e.contains(maskForNewInsertedReference))
        .map(e -> e.split("/")[e.split("/").length - 1])
        .collect(Collectors.toList());
  }

  private void deleteExistingTitleFileNames(TitleImgParams titleImgParams) {
    String folderForSearchTitleFile = titleImgParams.newsTitleImgFolder;
    String titleFileName = titleImgParams.newsTitleImgFileName;
    List<File> files = getTitleFileNameList(titleImgParams);
    for (File file : files) {
      file.delete();
    }
  }

  public String getTitleFileName(TitleImgParams titleImgParams) {
    List<File> files = getTitleFileNameList(titleImgParams);
    if (files.size() > 0) {
      return files.get(0).getName();
    }
    String titleFileName = titleImgParams.newsTitleImgFileName;
    return titleFileName.concat(".jpg");
  }

  public String getTitleFileResourceSource(NewsDto newsDto) throws IOException {
    return getTitleFileResourceSource(newsDto.getId(), newsDto.getResource(), newsDto.getNewsType());
  }

  public String getTitleFileResourceSource(Integer newsId, String resourcePath, NewsTypeEnum newsType) throws IOException {
    if (resourcePath == null) {
      return null;
    }
    resourcePath = getCorrectResourcesPath(resourcePath);
    String newsRoot = getNewsRootFolder(
        resourcePath,
        newsId);
    TitleImgParams titleImgParams = getTitleImgParams(newsId, newsRoot);
    String imageFileName = getTitleFileName(titleImgParams);
    String urlPath = getUrlPath(newsType);
    if (StringUtils.isEmpty(titleImgDir)) {
      return urlPath
          .concat("/")
          .concat(resourcePath)
          .concat(newsId.toString())
          .concat("/")
          .concat(imageFileName);
    } else {
      return urlPath
          .concat("/")
          .concat(titleImgDir)
          .concat("/")
          .concat(imageFileName);
    }
  }

  public NewsSyncDataDto changeLocalCode(NewsSyncDataDto newsSyncDataDto) {
    Map<String, String> localCodeMap = new HashMap<String, String>() {{
      put("ch", "cn");
      put("in", "id");
    }};
    for (NewsSyncDataDto.NewsVariantSyncData newsVariantSyncData : newsSyncDataDto.getVariantList()) {
      String localCode = newsVariantSyncData.getLanguage();
      String newLocalCode = localCodeMap.get(localCode);
      if (newLocalCode != null) {
        newsVariantSyncData.setLanguage(newLocalCode);
      }
    }
    return newsSyncDataDto;
  }

  private List<File> getTitleFileNameList(TitleImgParams titleImgParams) {
    String folderForSearchTitleFile = titleImgParams.newsTitleImgFolder;
    String titleFileName = titleImgParams.newsTitleImgFileName;
    File folder = new File(folderForSearchTitleFile);
    File[] files = folder.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(final File dir,
                            final String name) {
        return name.matches(titleFileName + "\\..*");
      }
    });
    if (files == null) return new ArrayList<>();
    return Arrays.asList((File[]) files);
  }

  private List<String> getReferenses(String html) {
    List<String> result = new ArrayList<>();
    if (StringUtils.isEmpty(html)) {
      return result;
    }
    Matcher matcher = Pattern.compile("(href\\s*=\\s*'([\\w[^']]*)')").matcher(html);
    while (matcher.find()) {
      result.add(matcher.group(2));
    }
    matcher = Pattern.compile("(href\\s*=\\s*\"([\\w[^\"]]*)\")").matcher(html);
    while (matcher.find()) {
      result.add(matcher.group(2));
    }
    matcher = Pattern.compile("(src\\s*=\\s*'([\\w[^']]*)')").matcher(html);
    while (matcher.find()) {
      result.add(matcher.group(2));
    }
    matcher = Pattern.compile("(src\\s*=\\s*\"([\\w[^\"]]*)\")").matcher(html);
    while (matcher.find()) {
      result.add(matcher.group(2));
    }
    return result;
  }

  private List<String> getImgReferenses(String html) {
    List<String> result = new ArrayList<>();
    if (StringUtils.isEmpty(html)) {
      return result;
    }
    Matcher matcher = Pattern.compile("(<\\s*img\\s{1}.*src\\s*=\\s*'([\\w[^']]*)')").matcher(html);
    while (matcher.find()) {
      result.add(matcher.group(2));
    }
    matcher = Pattern.compile("(<\\s*img\\s{1}.*src\\s*=\\s*\"([\\w[^\"]]*)\")").matcher(html);
    while (matcher.find()) {
      result.add(matcher.group(2));
    }
    return result;
  }

  private List<String> getDownloadableFileReferenses(String html) {
    List<String> result = new ArrayList<>();
    if (StringUtils.isEmpty(html)) {
      return result;
    }
    Matcher matcher = Pattern.compile("(<a\\s+href\\s*=\\s*[\"']([\\d\\D&&[^\"']]*)[\"']+[\\d\\D&&[^>]]*>)").matcher(html);
    while (matcher.find()) {
      String aTag = matcher.group(1);
      if (aTag.contains("download")) {
        result.add(matcher.group(2));
      }
    }
    return result;
  }

  private Map<String, String> getRefToResourcesPathReplacementMap(List<String> refList, String base, List<String> maskForProtectedReferenceList) {
    Map<String, String> result = new HashMap<>();
    for (String ref : refList) {
      boolean refIsMasked = false;
      for (String maskForProtectedReference : maskForProtectedReferenceList) {
        if (ref.matches("^[\\s/]*".concat(maskForProtectedReference).concat(".*"))) {
          refIsMasked = true;
          break;
        }
      }
      if (!refIsMasked) {
        result.put(ref, refToAbsoluteResourcesPath(ref, base));
      }
    }
    return result;
  }

  private String refToAbsoluteResourcesPath(String url, String base) {
    String origUrl = url;
    url = url.replaceAll("//", "/");
    if (url.trim().startsWith("http")) {
      return origUrl;
    }
    String firstSegment = url.replaceAll("^/", "").split("/")[0];
    if (firstSegment.matches("^\\w+\\.\\w+.*")) {
      return origUrl;
    }
    base = base.trim().replaceAll("/*$", "");
    url = url.replaceAll("^\\./", "/").trim();
    int partsBack = url.split("\\.\\./").length - 1;
    String[] parts = base.split("/");
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < parts.length - partsBack; i++) {
      result.append(parts[i]).append("/");
    }
    url = url.replaceAll("\\.\\./", "").replaceAll("^/", "");
    result.append(url);
    return result.toString();
  }

  public String getFolderForUploadImage() {
    return newsLocationDir.concat(tempImageUploadFolder); //  c:/DEVELOPING/NEWS/temp_img_upload/
  }

  public String getFolderForUploadFile() {
    return newsLocationDir.concat(tempFileUploadFolder); //  c:/DEVELOPING/NEWS/temp_file_upload/
  }

  public NewsTopicDto addTargetBlankToReference(NewsTopicDto newsTopicDto) {
    String html = newsTopicDto.getContent();
    if (StringUtils.isEmpty(html)) {
      return newsTopicDto;
    }
    List<String> externalTagAList = getTagsAWithExternalReferenceList(html);
    for (String aTag : externalTagAList) {
      String newATag = "";
      if (aTag.matches(".*\\starget\\s*=.*")) {
        if (!aTag.matches(".*\\starget\\s*=\\s*['\"]+_blank['\"]+.*")) {
          newATag = aTag.replaceAll("\\starget\\s*=\\s*['\"]+_[a-zA-Z]+['\"]+", " target='_blank'");
        }
      } else {
        newATag = aTag.replaceAll("^<a", "<a target='_blank' ");
      }
      if (!StringUtils.isEmpty(newATag)) {
        html = html.replace(aTag, newATag);
      }
    }
    newsTopicDto.setContent(html);
    return newsTopicDto;
  }

  private List<String> getTagsAWithExternalReferenceList(String html) {
    List<String> result = new ArrayList<>();
    if (StringUtils.isEmpty(html)) {
      return result;
    }
    Matcher matcher = Pattern.compile("(<a\\s+href\\s*=\\s*[\"']([\\d\\D&&[^\"']]*)[\"']+[\\d\\D&&[^>]]*>)").matcher(html);
    while (matcher.find()) {
      String aTag = matcher.group(1);
      if (aTag.matches(".*href\\s*=\\s*['\"]*http[s]?://.*")) {
        result.add(aTag);
      }
    }
    return result;
  }


}
