/* eslint-disable */

import {Controller, Post, Request, Logger, ConflictException} from '@nestjs/common';
import { UserService } from './user.service';

@Controller('user')

export class UserController{

    logger: Logger;

    constructor(private readonly userService: UserService) {
        this.logger = new Logger('UserController');
    }

    @Post('create')
    async create(@Request() req): Promise<any> {
        const newUser = req.body;

        try{
            const query = {email: newUser.email}
            const isUser = await this.userService.findOne(query);
            if(isUser){
                throw new ConflictException('Email already exists');
            }
            const user = await this.userService.create(newUser);
            return user;
        }catch(err){
            this.logger.error('Something wnet wrong. PLease try again later:', err);
            throw err;
        }
    }

}