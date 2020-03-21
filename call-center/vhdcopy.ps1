#VHD源存储信息，参数请修改。请注意源地址访问策略公共访问级别为Blob或容器（可以在Azure portal中修改）
$sourceStorageAccountName = "azurecreatingiamgedisks" #VHD所在存储账户名称
$sourceContainerName = "vhds" #VHD所在容器名称
$fileName="vhdimage20190412112221.vhd" #VHD文件名称

#目标存储信息，参数请修改 https://azurecreatingiamgedisks.blob.core.chinacloudapi.cn/vhds/vhdimage20190412112221.vhd
$destStorageAccountName ="imagesv2account" #VHD目标存储账号
$destStorageAccountKey ="S0B85vPXG6BFVbe8owpNJtlbOp414aII8p7LJYZDl4yyDIc546xy8j/5/H8OSv/cU44AqtUpTehzMu7gnC2CjQ==" #VHD目标存储账号KEY
$destContainerName ="images-container" #VHD目标容器名称

#Uri配置信息
$endpoint="core.chinacloudapi.cn"
$fileAbsoluteUri="https://"+$sourceStorageAccountName+".blob."+$endpoint+"/"+$sourceContainerName+"/"+$fileName
$destContext = New-AzureStorageContext -StorageAccountName $destStorageAccountName -StorageAccountKey $destStorageAccountKey  -Endpoint $endpoint
#开始复制
Start-AzureStorageBlobCopy  -AbsoluteUri $fileAbsoluteUri -DestContext $destContext -DestContainer $destContainerName -DestBlob $fileName -Force

#检查Blob Copy的结果 
Get-AzureStorageBlobCopyState -Blob $fileName -Context $destContext -Container $destContainerName -WaitForComplete 

#Write-Host "TODO//开始创建虚拟机"