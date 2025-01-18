import { Injectable, Inject, Logger } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Product, ProductDocument } from './model/product.model';

@Injectable()
export class ProductService {
  logger: Logger;

  constructor(
    @InjectModel(Product.name) private productModel: Model<ProductDocument>,
  ) {
    this.logger = new Logger(ProductService.name);
  }

  async findOne(query: any): Promise<any> {
    return await this.productModel.findOne(query).exec();
  }

  async create(product: any): Promise<any> {
    this.logger.log('Creating product.');

    const newProduct = new this.productModel(product);
    return newProduct.save();
  }
}
