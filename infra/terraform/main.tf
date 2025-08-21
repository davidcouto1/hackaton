# Exemplo de provisionamento de infraestrutura para rodar a API Java com Docker em uma VM na Azure

provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "hackaton_rg" {
  name     = "hackaton-rg"
  location = "East US"
}

resource "azurerm_virtual_network" "hackaton_vnet" {
  name                = "hackaton-vnet"
  address_space       = ["10.0.0.0/16"]
  location            = azurerm_resource_group.hackaton_rg.location
  resource_group_name = azurerm_resource_group.hackaton_rg.name
}

resource "azurerm_subnet" "hackaton_subnet" {
  name                 = "hackaton-subnet"
  resource_group_name  = azurerm_resource_group.hackaton_rg.name
  virtual_network_name = azurerm_virtual_network.hackaton_vnet.name
  address_prefixes     = ["10.0.1.0/24"]
}

resource "azurerm_network_interface" "hackaton_nic" {
  name                = "hackaton-nic"
  location            = azurerm_resource_group.hackaton_rg.location
  resource_group_name = azurerm_resource_group.hackaton_rg.name

  ip_configuration {
    name                          = "internal"
    subnet_id                     = azurerm_subnet.hackaton_subnet.id
    private_ip_address_allocation = "Dynamic"
  }
}

resource "azurerm_linux_virtual_machine" "hackaton_vm" {
  name                = "hackaton-vm"
  resource_group_name = azurerm_resource_group.hackaton_rg.name
  location            = azurerm_resource_group.hackaton_rg.location
  size                = "Standard_B1s"
  admin_username      = "azureuser"
  network_interface_ids = [azurerm_network_interface.hackaton_nic.id]
  admin_password      = "P@ssw0rd1234!"
  disable_password_authentication = false

  os_disk {
    caching              = "ReadWrite"
    storage_account_type = "Standard_LRS"
    name                 = "osdisk"
  }

  source_image_reference {
    publisher = "Canonical"
    offer     = "UbuntuServer"
    sku       = "18.04-LTS"
    version   = "latest"
  }

  custom_data = filebase64("${path.module}/cloud-init.txt")
}

resource "azurerm_public_ip" "hackaton_public_ip" {
  name                = "hackaton-public-ip"
  location            = azurerm_resource_group.hackaton_rg.location
  resource_group_name = azurerm_resource_group.hackaton_rg.name
  allocation_method   = "Dynamic"
}

resource "azurerm_network_interface_backend_address_pool_association" "hackaton_nic_ip" {
  network_interface_id    = azurerm_network_interface.hackaton_nic.id
  ip_configuration_name   = "internal"
  backend_address_pool_id = azurerm_public_ip.hackaton_public_ip.id
}

output "public_ip_address" {
  value = azurerm_public_ip.hackaton_public_ip.ip_address
}

