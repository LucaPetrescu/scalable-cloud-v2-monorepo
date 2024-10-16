import {
  Controller,
  Post,
  Request,
  Logger,
  Get,
  Query,
  Res,
  HttpStatus,
  HttpException,
} from '@nestjs/common';
import { ProductService } from './product.service';

@Controller('product')
export class ProductController {
  logger: Logger;

  constructor(private readonly productService: ProductService) {
    this.logger = new Logger('ProductController');
  }

  @Post('create')
  async create(@Request() req, @Res() res): Promise<any> {
    const newProduct = req.body;

    try {
      const query = {
        name: newProduct.productName,
        barCode: newProduct.barCode,
      };

      const isProduct = await this.productService.findOne(query);

      if (isProduct) {
        throw new HttpException(
          'Product already Exists. Check the name or the barcode',
          HttpStatus.CONFLICT,
        );
      }

      const product = this.productService.create(newProduct);
      res.status(HttpStatus.CREATED).send(product);
      return product;
    } catch (err) {
      this.logger.error('Something went wrong. Please try again later:', err);
      res.status(HttpStatus.INTERNAL_SERVER_ERROR).send(err);
    }
  }

  @Get('getProduct')
  async getProduct(
    @Query('productId') productId: string,
    @Res() res,
  ): Promise<any> {
    try {
      const query = { productId: productId };
      const product = await this.productService.findOne(query);

      if (!product) {
        throw new HttpException('Product does not exist', HttpStatus.NOT_FOUND);
      }

      res.status(HttpStatus.OK).send(product);
      return product;
    } catch (err) {
      this.logger.error('Something went wrong. Please try again: ', err);
      res.status(HttpStatus.INTERNAL_SERVER_ERROR).send(err);
    }
  }

  @Get('getProductInventory')
  async getProductInventory(
    @Query('productId') productId: string,
    @Res() res,
  ): Promise<any> {
    try {
      const query = { productId: productId };
      const product = await this.productService.findOne(query);

      if (!product) {
        throw new HttpException('Product does not exist', HttpStatus.NOT_FOUND);
      }

      let productInventory = product.quantity;

      res.status(HttpStatus.OK).send({ quantity: productInventory });

      return { quantity: productInventory };
    } catch (err) {
      this.logger.error('Something went wrong. Please try again: ', err);
      res.status(HttpStatus.INTERNAL_SERVER_ERROR).send(err);
    }
  }
}
